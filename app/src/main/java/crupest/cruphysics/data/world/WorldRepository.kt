package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.nowLong
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WorldRepository(context: Context,
                      onFinishReadLatestListener: ((WorldRecordEntity?) -> Unit)?,
                      onFinishReadAllListener: () -> Unit
) {
    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()
    private val cacheLock = Any()
    private val cache: MutableList<WorldRecordEntity> = mutableListOf()

    private val workingExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        if (onFinishReadLatestListener != null)
            workingExecutor.submit {
                onFinishReadLatestListener.invoke(dao.getLatestRecord())
            }

        workingExecutor.submit {
            val records = dao.getRecords()
            synchronized(cacheLock) {
                cache.addAll(records)
            }
            onFinishReadAllListener()
        }
    }

    //events
    var addCompleteListener: (() -> Unit)? = null
    var latestCameraUpdateCompleteListener: (() -> Unit)? = null
    var timestampUpdateCompleteListener: ((Int) -> Unit)? = null

    private fun compressThumbnail(thumbnail: Bitmap): ByteArray {
        return ByteArrayOutputStream().also {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
    }

    val recordCount: Int
        get() = synchronized(cacheLock) { cache.size }

    fun getRecord(position: Int) = synchronized(cacheLock) { cache[position] }

    fun addRecord(worldData: WorldData, cameraData: CameraData, thumbnailBitmap: Bitmap) {
        workingExecutor.submit {

            val record = WorldRecordEntity().apply {
                timestamp = nowLong()
                world = worldData.toJson()
                camera = cameraData.toJson()
                thumbnail = compressThumbnail(thumbnailBitmap)
            }

            synchronized(cacheLock) {
                cache.add(0, record)
            }

            dao.insert(record)

            addCompleteListener?.invoke()
        }
    }

    fun updateLatestRecordCamera(camera: CameraData, thumbnail: Bitmap) {
        workingExecutor.submit {
            synchronized(cacheLock) {
                cache.firstOrNull()
            }?.also {
                it.camera = camera.toJson()
                it.thumbnail = compressThumbnail(thumbnail)
                dao.update(it)
            }

            latestCameraUpdateCompleteListener?.invoke()
        }
    }

    fun updateTimestamp(position: Int) {
        workingExecutor.submit {

            val record = synchronized(cacheLock) {
                val r = cache.removeAt(position)
                cache.add(0, r)
                r
            }

            record.timestamp = nowLong()
            dao.update(record)

            timestampUpdateCompleteListener?.invoke(position)
        }
    }

    fun closeAndWait() {
        workingExecutor.shutdown()
        workingExecutor.awaitTermination(1, TimeUnit.MINUTES)
    }
}
