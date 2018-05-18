package crupest.cruphysics.data.world

import android.content.Context
import android.graphics.Bitmap
import crupest.cruphysics.Event
import crupest.cruphysics.IWorldRecordFileResolver
import crupest.cruphysics.utility.sha1Hex
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WorldRepository(context: Context, val fileResolver: IWorldRecordFileResolver) : AutoCloseable {
    private val dao: WorldRecordDao = WorldDatabase.getInstance(context).worldRecordDao()
    private val cacheLock = Any()
    private lateinit var cache: List<WorldRecord>

    init {
        thread {
            synchronized(cacheLock) {
                cache = dao.getRecords()
            }
        }.join()
    }

    class AddCompleteEventArgs

    val addCompleteEvent = Event<AddCompleteEventArgs>()

    private class SavingTask(
            val world: String,
            val thumbnail: Bitmap
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

    private fun doSaving(savingTask: SavingTask) {
        //Save world.
        val world = savingTask.world
        val worldFileName = world.sha1Hex()
        fileResolver.getWorldFile(worldFileName).writeText(world)


        //Save thumbnail.
        val thumbnail = savingTask.thumbnail
        //create temp file
        val thumbnailTempFile = fileResolver.getThumbnailFile("temp")
        //compress and save to temp file
        thumbnailTempFile.outputStream().use {
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        //calculate sha1 from temp file as the final file name
        val thumbnailFileName = thumbnailTempFile.inputStream().use { it.sha1Hex() }
        thumbnailTempFile.renameTo(fileResolver.getThumbnailFile(thumbnailFileName))

        dao.insert(WorldRecord(worldFile = worldFileName, thumbnailFile = thumbnailFileName))

        synchronized(cacheLock) {
            cache = dao.getRecords()
        }

        addCompleteEvent.raise(AddCompleteEventArgs())
    }

    val recordCount: Int
        get() = synchronized(cacheLock) { cache.size }

    fun getRecord(position: Int) = synchronized(cacheLock) { cache[position] }

    fun addRecord(world: String, thumbnail: Bitmap) {
        savingTaskQueue.put(SavingTask(world, thumbnail))
    }

    override fun close() {
        stop.set(true)
    }
}
