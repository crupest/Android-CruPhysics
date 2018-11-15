package crupest.cruphysics.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.*
import crupest.cruphysics.component.delegate.IDrawDelegate
import crupest.cruphysics.data.world.WorldDatabase
import crupest.cruphysics.data.world.WorldRecordDao
import crupest.cruphysics.data.world.entity.ProcessedWorldRecordWithoutId
import crupest.cruphysics.data.world.entity.WorldRecordEntity
import crupest.cruphysics.data.world.entity.process
import crupest.cruphysics.physics.fromData
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.fromData
import crupest.cruphysics.serialization.toData
import crupest.cruphysics.serialization.toJson
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.compressToPng
import crupest.cruphysics.utility.setInterval
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import kotlinx.coroutines.*
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2
import java.util.*
import kotlin.properties.Delegates

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val THUMBNAIL_WIDTH = 1000
        private const val THUMBNAIL_HEIGHT = 1000
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val dao: WorldRecordDao = WorldDatabase.getInstance(application.applicationContext).worldRecordDao()

    private var task: ScheduleTask? = null

    private val recordListInternal = mutableListOf<ProcessedWorldRecordWithoutId>()
    private val recordListChangeProcessor: PublishProcessor<ListChange> = PublishProcessor.create()


    private val world: World = World()

    private val worldRepaintListeners: MutableList<() -> Unit> = mutableListOf()
    private val worldHistoryScrollToTopListeners: MutableList<() -> Unit> = mutableListOf()
    private val cameraChangedListeners: MutableList<() -> Unit> = mutableListOf()

    private var camera: CameraData by Delegates.observable(CameraData()) { _, _, it ->
        notifyCameraChanged()
        drawWorldDelegateInternal.setScale(it.scale)
    }

    private val drawWorldDelegateInternal: DrawWorldDelegate = DrawWorldDelegate()
    private val worldStateInternal: MutableLiveData<Boolean> = mutableLiveDataWithDefault(false)

    val recordList: List<ProcessedWorldRecordWithoutId> get() = recordListInternal
    val recordListChangeFlow: Flowable<ListChange> get() = recordListChangeProcessor

    val drawWorldDelegate: IDrawDelegate get() = drawWorldDelegateInternal
    val worldState: LiveData<Boolean> get() = worldStateInternal


    init {
        uiScope.launch {
            val latestRecordDeferred = async(Dispatchers.IO) {
                dao.getLatestRecord()?.process()
            }
            val recordsDeferred = async(Dispatchers.IO) {
                dao.getAllRecords().map { it.process() }
            }

            val latestRecord = latestRecordDeferred.await()
            if (latestRecord != null) {
                recoverFrom(latestRecord.world, latestRecord.camera)
                recordListInternal.add(latestRecord)
                recordListChangeProcessor.offer(ListItemAdd(0))
            }

            val records = recordsDeferred.await()
            if (!records.isEmpty()) {
                recordListInternal.addAll(records.subList(1, records.size))
                recordListChangeProcessor.offer(ListRangeAdd(1, records.size))
            }
        }
    }

    override fun onCleared() {
        drawWorldDelegateInternal.onClear()
    }

    private fun generateThumbnail(): Bitmap {
        val camera = this.camera.copy(scale = this.camera.scale * 0.5)
        return Bitmap.createBitmap(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, Bitmap.Config.ARGB_8888).applyCanvas {
            concat(camera.fromData(THUMBNAIL_WIDTH.toFloat() / 2.0f, THUMBNAIL_HEIGHT.toFloat() / 2.0f))
            drawWorldDelegateInternal.draw(this)
        }
    }

    private fun recoverFrom(worldData: WorldData, cameraData: CameraData) {
        world.removeAllBodiesAndJoints()
        worldData.fromData(world)
        drawWorldDelegateInternal.clearAndRegister(world.bodies)
        camera = cameraData
    }

    private fun createNewRecordFromCurrent() {
        uiScope.launch {
            val timestamp = Date()
            val worldData = world.toData()
            val cameraData = camera.copy()
            val thumbnail = generateThumbnail()
            recordListInternal.add(0, ProcessedWorldRecordWithoutId(timestamp, worldData, cameraData, thumbnail))
            recordListChangeProcessor.offer(ListItemAdd(0))
            notifyWorldHistoryScrollToTop()

            launch(Dispatchers.IO) {
                dao.insert(WorldRecordEntity().apply {
                    this.timestamp = timestamp.time
                    this.world = worldData.toJson()
                    this.camera = cameraData.toJson()
                    this.thumbnail = thumbnail.compressToPng()
                })
            }
        }
    }

    private fun updateCurrentRecordCamera(cameraData: CameraData) {
        uiScope.launch {

            camera = cameraData

            if (world.isEmpty)
                return@launch

            if (recordListInternal.isEmpty())
                return@launch

            val newTimestamp = Date()
            val thumbnail = generateThumbnail()

            val oldItem = recordListInternal[0]
            recordListInternal.removeAt(0)
            recordListInternal.add(0, oldItem.copy(timestamp = newTimestamp, camera = camera, thumbnail = thumbnail))

            recordListChangeProcessor.offer(ListItemContentChange(0))

            launch(Dispatchers.IO) {
                dao.updateCamera(oldItem.timestamp.time, newTimestamp.time, camera.toJson(), thumbnail.compressToPng())
            }
        }
    }


    private fun <T : Function<Unit>> registerListener(lifecycleOwner: LifecycleOwner, listeners: MutableList<T>, listener: T) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun addListener() {
                listeners.add(listener)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun removeListener() {
                listeners.remove(listener)
            }
        })
    }

    fun registerWorldRepaintListener(lifecycleOwner: LifecycleOwner, listener: () -> Unit) {
        registerListener(lifecycleOwner, worldRepaintListeners, listener)
    }

    fun registerWorldHistoryScrollToTopListener(lifecycleOwner: LifecycleOwner, listener: () -> Unit) {
        registerListener(lifecycleOwner, worldHistoryScrollToTopListeners, listener)
    }

    private fun notifyRepaint() {
        worldRepaintListeners.forEach { it.invoke() }
    }

    private fun notifyWorldHistoryScrollToTop() {
        worldHistoryScrollToTopListeners.forEach { it.invoke() }
    }

    private fun notifyCameraChanged() {
        cameraChangedListeners.forEach { it.invoke() }
    }

    private fun createNewWorld() {
        pauseWorld()
        world.removeAllBodiesAndJoints()
        drawWorldDelegateInternal.unregisterAllBody()
    }

    fun createNewWorldAndResetCamera() {
        createNewWorld()
        camera = CameraData()
        notifyRepaint()
    }

    fun runWorld(): Boolean =
            if (task == null) {
                task = setInterval(1.0 / 60.0) {
                    world.update(1.0 / 60.0)
                    notifyRepaint()
                }
                worldStateInternal.value = true
                true
            } else false

    fun pauseWorld() =
            if (task != null) {
                task!!.cancel()
                task = null
                worldStateInternal.value = false
                createNewRecordFromCurrent()
                true
            } else false

    fun addBody(body: Body) {
        world.addBody(body)
        drawWorldDelegateInternal.registerBody(body)
        createNewRecordFromCurrent()
        notifyRepaint()
    }

    fun removeBody(body: Body) {
        world.removeBody(body)
        drawWorldDelegateInternal.unregisterBody(body)
        if (!world.isEmpty)
            createNewRecordFromCurrent()
        notifyRepaint()
    }


    fun recoverFromRecordAndUpdateTimestamp(record: ProcessedWorldRecordWithoutId) {
        uiScope.launch {
            recoverFrom(record.world, record.camera)

            val itemIndex = recordListInternal.indexOf(record)
            if (itemIndex == -1)
                throw IllegalStateException("Record not in list.")

            val newTimestamp = Date()
            recordListInternal.removeAt(itemIndex)
            recordListInternal.add(0, record.copy(timestamp = newTimestamp))

            recordListChangeProcessor.offer(ListItemMove(itemIndex, 0))
            recordListChangeProcessor.offer(ListItemContentChange(0))

            notifyWorldHistoryScrollToTop()
            notifyRepaint()

            launch(Dispatchers.IO) {
                dao.updateTimestamp(record.timestamp.time, newTimestamp.time)
            }
        }
    }

    fun bodyHitTest(x: Double, y: Double): Body? =
            world.bodies.firstOrNull { it.contains(Vector2(x, y)) }

    fun getBodyById(id: UUID) = world.bodies.find { it.id == id }
            ?: throw IllegalArgumentException("No body has id as $id")

    fun updateBody(body: Body) {
        drawWorldDelegateInternal.updateBody(body)
        notifyRepaint()
    }

    private fun CameraData.translateCreate(x: Double, y: Double): CameraData {
        return this.copy(translation = Vector2Data(this.translation.x + x / this.scale, this.translation.y + y / this.scale))
    }

    private fun CameraData.scaleCreate(scale: Double): CameraData {
        return this.copy(scale = this.scale * scale)
    }

//camera

    fun registerCameraChangedListener(lifecycleOwner: LifecycleOwner, listener: () -> Unit) {
        registerListener(lifecycleOwner, cameraChangedListeners, listener)
    }

    fun getCameraMatrix(matrix: Matrix, centerX: Float, centerY: Float) {
        camera.fromData(matrix, centerX, centerY)
    }

    fun cameraPostTranslate(x: Float, y: Float) {
        updateCurrentRecordCamera(camera.translateCreate(x.toDouble(), y.toDouble()))
        notifyRepaint()
    }

    fun cameraPostScale(scale: Float) {
        updateCurrentRecordCamera(camera.scaleCreate(scale.toDouble()))
        notifyRepaint()
    }
}
