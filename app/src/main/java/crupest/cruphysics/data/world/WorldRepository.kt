package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.IWorldRecordFileResolver
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.nowLong
import crupest.cruphysics.utility.sha1Hex
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WorldRepository(context: Context, val fileResolver: IWorldRecordFileResolver) {
    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()
    private val cacheLock = Any()
    private lateinit var cache: List<WorldRecord>

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
        thread {
            synchronized(cacheLock) {
                cache = dao.getRecords()
            }
        }.join()
    }

    //events
    open class UiNotifyEventArgs(private val latch: CountDownLatch) {
        fun notifyDone() {
            latch.countDown()
        }
    }

    class AddCompleteEventArgs(latch: CountDownLatch) : UiNotifyEventArgs(latch)
    class LatestCameraUpdateCompleteEventArgs(latch: CountDownLatch) : UiNotifyEventArgs(latch)
    class TimestampUpdateCompleteEventArgs(latch: CountDownLatch, val oldPosition: Int) : UiNotifyEventArgs(latch)

    var addCompleteListener: ((AddCompleteEventArgs) -> Unit)? = null
    var latestCameraUpdateCompleteListener: ((LatestCameraUpdateCompleteEventArgs) -> Unit)? = null
    var timestampUpdateCompleteListener: ((TimestampUpdateCompleteEventArgs) -> Unit)? = null

    private fun generateThumbnailFile(thumbnail: Bitmap): String {
        //Save thumbnail.
        //create temp file
        val thumbnailTempFile = fileResolver.getThumbnailFile("temp")
        //compress and save to temp file
        thumbnailTempFile.outputStream().use {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        //calculate sha1 from temp file as the final file name
        val thumbnailFileName = thumbnailTempFile.inputStream().use { it.sha1Hex() }
        thumbnailTempFile.renameTo(fileResolver.getThumbnailFile(thumbnailFileName))

        return thumbnailFileName
    }

    private fun runAndWaitForUiThread(block: (CountDownLatch) -> Unit) {
        val latch = CountDownLatch(1)
        block(latch)
        latch.await()
    }

    val recordCount: Int
        get() = synchronized(cacheLock) { cache.size }

    fun getRecord(position: Int) = synchronized(cacheLock) { cache[position] }

    fun addRecord(worldData: WorldData, cameraData: CameraData, thumbnail: Bitmap) {
        taskQueue.offer {
            dao.insert(WorldRecord(
                    world = worldData.toJson(),
                    camera = cameraData.toJson(),
                    thumbnailFile = generateThumbnailFile(thumbnail))
            )

            synchronized(cacheLock) {
                cache = dao.getRecords()
            }

            runAndWaitForUiThread {
                addCompleteListener?.invoke(AddCompleteEventArgs(it))
            }
        }
    }

    fun updateLatestRecordCamera(camera: CameraData, thumbnail: Bitmap) {
        taskQueue.offer {
            synchronized(cacheLock) {
                cache.firstOrNull()?.also {
                    it.camera = camera.toJson()
                    fileResolver.getThumbnailFile(it.thumbnailFile).delete()
                    it.thumbnailFile = generateThumbnailFile(thumbnail)
                    dao.update(it)
                }
            }

            runAndWaitForUiThread {
                latestCameraUpdateCompleteListener?.invoke(
                        LatestCameraUpdateCompleteEventArgs(it))
            }
        }
    }

    fun updateTimestamp(position: Int) {
        taskQueue.offer {
            synchronized(cacheLock) {
                val record = cache[position]
                record.timestamp = nowLong()
                dao.update(record)
                cache = dao.getRecords()
            }

            runAndWaitForUiThread {
                timestampUpdateCompleteListener?.invoke(TimestampUpdateCompleteEventArgs(it, position))
            }
        }
    }

    fun closeAndWait() {
        stop.set(true)
        workingThread.join()
    }
}
