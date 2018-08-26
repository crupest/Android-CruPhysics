package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.nowLong
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WorldRepository(context: Context,
                      onFinishReadLatestListener: ((WorldRecordEntity?) -> Unit)?,
                      onFinishReadAllListener: () -> Unit
) {
    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()
    private val cacheLock = Any()
    private val cache: MutableList<WorldRecordEntity> = mutableListOf()

    private val stop = AtomicBoolean(false)
    private val taskQueue: Queue<() -> Unit> = ConcurrentLinkedQueue()
    private val workingThread: Thread = thread(start = true, name = "SavingWorldThread") {
        while (true) {
            if (stop.get() && taskQueue.isEmpty())
                return@thread
            taskQueue.poll()?.invoke()
        }
    }

    init {
        if (onFinishReadLatestListener != null)
            taskQueue.offer {
                onFinishReadLatestListener.invoke(dao.getLatestRecord())
            }

        taskQueue.offer {
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

    fun addRecord(worldData: WorldData, cameraData: CameraData, thumbnail: Bitmap) {
        taskQueue.offer {

            val record = WorldRecordEntity(
                    timestamp = nowLong(),
                    world = worldData.toJson(),
                    camera = cameraData.toJson(),
                    thumbnail = compressThumbnail(thumbnail)
            )

            synchronized(cacheLock) {
                cache.add(0, record)
            }

            dao.insert(record)

            addCompleteListener?.invoke()
        }
    }

    fun updateLatestRecordCamera(camera: CameraData, thumbnail: Bitmap) {
        taskQueue.offer {
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
        taskQueue.offer {

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
        stop.set(true)
        workingThread.join()
    }
}
