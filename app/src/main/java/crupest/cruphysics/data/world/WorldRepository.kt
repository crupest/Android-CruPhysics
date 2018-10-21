package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import crupest.cruphysics.data.world.entity.WorldRecordEntity
import crupest.cruphysics.data.world.processed.ProcessedWorldRecordForLatest
import crupest.cruphysics.data.world.processed.ProcessedWorldRecordForHistory
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toJson
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subjects.MaybeSubject
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WorldRepository(context: Context) {

    private val workingExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()

    private val latestRecordMaybeSubject: MaybeSubject<ProcessedWorldRecordForLatest> = MaybeSubject.create()

    private val recordForHistoryListProcessor: BehaviorProcessor<List<ProcessedWorldRecordForHistory>> = BehaviorProcessor.create()

    private var currentId: Long = 0

    init {
        workingExecutor.submit {
            val record = dao.getLatestRecord()
            if (record != null)
                latestRecordMaybeSubject.onSuccess(
                        ProcessedWorldRecordForLatest(record.world.fromJson(), record.camera.fromJson()))
            else
                latestRecordMaybeSubject.onComplete()
        }

        workingExecutor.submit {
            val list = dao.getAllRecordForThumbnail().map {
                ProcessedWorldRecordForHistory(currentId++, Date(it.timestamp), it.world.fromJson(), it.camera.fromJson(), decompressThumbnail(it.thumbnail))
            }
            recordForHistoryListProcessor.onNext(list)
        }
    }

    private fun compressThumbnail(thumbnail: Bitmap): ByteArray {
        return ByteArrayOutputStream().also {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
    }

    private fun decompressThumbnail(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
                ?: throw RuntimeException("Failed to decompress thumbnail.")
    }

    fun getLatestRecord(): Maybe<ProcessedWorldRecordForLatest> {
        return latestRecordMaybeSubject
    }

    fun getRecordListFlow(): Flowable<List<ProcessedWorldRecordForHistory>> {
        return recordForHistoryListProcessor
    }

    fun createRecord(timestamp: Date, worldData: WorldData, cameraData: CameraData, thumbnailBitmap: Bitmap) {
        val oldList = recordForHistoryListProcessor.value
                ?: throw IllegalStateException("The history list hasn't been loaded.")

        val newList = oldList.toMutableList()
        newList.add(0, ProcessedWorldRecordForHistory(currentId++, timestamp, worldData, cameraData, thumbnailBitmap))

        recordForHistoryListProcessor.onNext(newList)

        workingExecutor.submit {
            val record = WorldRecordEntity().apply {
                this.timestamp = timestamp.time
                world = worldData.toJson()
                camera = cameraData.toJson()
                thumbnail = compressThumbnail(thumbnailBitmap)
            }
            dao.insert(record)
        }
    }

    fun updateTimestamp(oldTimestamp: Date, newTimestamp: Date) {
        val oldList = recordForHistoryListProcessor.value
                ?: throw IllegalStateException("The history list hasn't been loaded.")

        val newList = oldList.toMutableList()
        val itemIndex = newList.indexOfFirst {
            it.timestamp == oldTimestamp
        }
        val item = newList[itemIndex]
        newList.removeAt(itemIndex)
        newList.add(0, item.copy(timestamp = newTimestamp))

        recordForHistoryListProcessor.onNext(newList)

        workingExecutor.submit {
            dao.updateTimestamp(oldTimestamp.time, newTimestamp.time)
        }
    }

    fun updateLatestCamera(newTimestamp: Date, camera: CameraData, thumbnail: Bitmap) {
        val oldList = recordForHistoryListProcessor.value
                ?: throw IllegalStateException("The history list hasn't been loaded.")

        if (!oldList.isEmpty()) {
            val oldItem = oldList[0]

            val newList = oldList.toMutableList()
            newList.removeAt(0)
            newList.add(0, oldItem.copy(timestamp = newTimestamp, camera = camera, thumbnail = thumbnail))

            recordForHistoryListProcessor.onNext(newList)

            workingExecutor.submit {
                dao.updateCamera(oldItem.timestamp.time, newTimestamp.time, camera.toJson(), compressThumbnail(thumbnail))
            }
        }
    }

    fun closeAndWait() {
        workingExecutor.shutdown()
        workingExecutor.awaitTermination(1, TimeUnit.MINUTES)
    }
}
