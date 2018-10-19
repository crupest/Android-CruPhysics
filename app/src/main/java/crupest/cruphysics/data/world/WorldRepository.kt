package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.nowLong
import io.reactivex.Flowable
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WorldRepository(
        context: Context,
        onFinishReadLatestListener: ((WorldRecordEntity?) -> Unit)
) {

    private val workingExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()

    init {
        workingExecutor.submit {
            onFinishReadLatestListener.invoke(dao.getLatestRecord())
        }
    }

    val records : Flowable<List<WorldRecordEntity>> = dao.getRecords()

    private fun compressThumbnail(thumbnail: Bitmap): ByteArray {
        return ByteArrayOutputStream().also {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
    }

    fun addRecord(worldData: WorldData, cameraData: CameraData, thumbnailBitmap: Bitmap) {
        workingExecutor.submit {
            val record = WorldRecordEntity().apply {
                timestamp = nowLong()
                world = worldData.toJson()
                camera = cameraData.toJson()
                thumbnail = compressThumbnail(thumbnailBitmap)
            }
            dao.insert(record)
        }
    }

    fun updateRecordCamera(record: WorldRecordEntity, camera: CameraData, thumbnail: Bitmap) {
        workingExecutor.submit {
            record.apply {
                this.camera = camera.toJson()
                this.thumbnail = compressThumbnail(thumbnail)
                dao.update(this)
            }
        }
    }

    fun updateTimestamp(record: WorldRecordEntity) {
        workingExecutor.submit {
            record.timestamp = nowLong()
            dao.update(record)
        }
    }

    fun closeAndWait() {
        workingExecutor.shutdown()
        workingExecutor.awaitTermination(1, TimeUnit.MINUTES)
    }
}
