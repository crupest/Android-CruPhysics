package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.Event
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WorldRepository(context: Context): AutoCloseable {
    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()
    private val cacheLock = Any()
    private var cache: List<WorldRecord> = dao.getRecords()

    class AddCompleteEventArgs

    val addCompleteEvent = Event<AddCompleteEventArgs>()

    private class SavingTask(
            val world: String,
            val thumbnail: Bitmap,
            val worldDir: File,
            val thumbnailDir: File
    )

    private val stop = AtomicBoolean(false)
    private val savingTaskQueue: BlockingQueue<SavingTask> = LinkedBlockingQueue()
    private val savingWorkingThread: Thread = thread(start = true, name = "SavingWorldThread") {
        while (true) {
            if (stop.get() && savingTaskQueue.isEmpty())
                return@thread
            doSaving(savingTaskQueue.take())
        }
    }


    private fun invalidateCache() {
        synchronized(cacheLock) {
            cache = dao.getRecords()
        }
    }

    private fun doSaving(savingTask: SavingTask) {
        //Save world.
        val world = savingTask.world
        val worldFileName = DigestUtils.sha1Hex(world)
        savingTask.worldDir.resolve(worldFileName).writeText(world)


        //Save thumbnail.
        val thumbnail = savingTask.thumbnail
        //create temp file
        val thumbnailTempFile = savingTask.thumbnailDir.resolve("temp")
        //compress and save to temp file
        thumbnailTempFile.outputStream().use {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        //calculate sha1 from temp file as the final file name
        val thumbnailFileName = thumbnailTempFile.inputStream().use { DigestUtils.sha1Hex(it) }
        thumbnailTempFile.renameTo(savingTask.thumbnailDir.resolve(thumbnailFileName))

        dao.insert(WorldRecord(worldFile = worldFileName, thumbnailFile = thumbnailFileName))

        invalidateCache()

        addCompleteEvent.raise(AddCompleteEventArgs())
    }

    val recordCount: Int
        get() = synchronized(cacheLock) { cache.size }

    fun getRecord(position: Int) = synchronized(cacheLock) { cache[position] }

    fun addRecord(world: String, thumbnail: Bitmap, worldDir: File, thumbnailDir: File) {
        savingTaskQueue.put(SavingTask(world, thumbnail, worldDir, thumbnailDir))
    }

    override fun close() {
        stop.set(true)
    }
}
