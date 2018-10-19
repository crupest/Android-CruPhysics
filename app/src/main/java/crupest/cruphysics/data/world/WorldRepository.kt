package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.toJson
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WorldRepository(
        context: Context,
        onFinishReadLatestListener: ((WorldRecordEntity?) -> Unit)
) {
    data class UpdateCameraInfo(val cameraData: CameraData, val thumbnail: Bitmap)

    private val workingExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private var currentCameraUpdateFlow: PublishProcessor<UpdateCameraInfo>? = null

    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()

    private val scheduler: Scheduler = Schedulers.from(workingExecutor)

    init {
        workingExecutor.submit {
            onFinishReadLatestListener.invoke(dao.getLatestRecord())
        }
    }

    val records: Flowable<List<WorldRecordEntity>> = dao.getRecords()

    private fun compressThumbnail(thumbnail: Bitmap): ByteArray {
        return ByteArrayOutputStream().also {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
    }

    private fun updateCameraByTimestamp(timestamp: Long, camera: CameraData, thumbnail: Bitmap) {
        dao.updateCameraByTimestamp(timestamp, camera.toJson(), compressThumbnail(thumbnail))
    }

    fun createRecord(timestamp: Long, worldData: WorldData, cameraData: CameraData, thumbnailBitmap: Bitmap) {
        workingExecutor.submit {
            val record = WorldRecordEntity().apply {
                this.timestamp = timestamp
                world = worldData.toJson()
                camera = cameraData.toJson()
                thumbnail = compressThumbnail(thumbnailBitmap)
            }
            dao.insert(record)
        }
    }

    fun updateTimestamp(id: Long, timestamp: Long) {
        workingExecutor.submit {
            dao.updateTimestamp(id, timestamp)
        }
    }

    fun createNewCameraUpdateFlow(timestamp: Long): Subscriber<UpdateCameraInfo> {
        currentCameraUpdateFlow?.apply {
            onComplete()
        }
        currentCameraUpdateFlow = PublishProcessor.create<UpdateCameraInfo>().apply {
            this.onBackpressureLatest()
                    .observeOn(scheduler)
                    .subscribe {
                        updateCameraByTimestamp(timestamp, it.cameraData, it.thumbnail)
                    }
        }

        return currentCameraUpdateFlow!!
    }

    fun closeAndWait() {
        workingExecutor.shutdown()
        workingExecutor.awaitTermination(1, TimeUnit.MINUTES)
    }
}
