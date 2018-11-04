package crupest.cruphysics.viewmodel

import android.app.Application
import androidx.lifecycle.*
import crupest.cruphysics.component.delegate.DrawWorldDelegate
import crupest.cruphysics.data.world.WorldRepository
import crupest.cruphysics.data.world.processed.ProcessedWorldRecordForHistory
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.fromData
import crupest.cruphysics.serialization.toData
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2
import java.lang.IllegalArgumentException
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val worldRepository: WorldRepository =
            WorldRepository(getApplication<Application>().applicationContext).apply {
                getLatestRecord().observeOn(AndroidSchedulers.mainThread()).subscribe {
                    recoverFrom(it.world, it.camera)
                }
            }

    private var task: ScheduleTask? = null


    private val world: World = World()

    private val worldRepaintListeners: MutableList<() -> Unit> = mutableListOf()
    private val worldHistoryScrollToTopListeners: MutableList<() -> Unit> = mutableListOf()

    private val cameraInternal: MutableLiveData<CameraData> = mutableLiveDataWithDefault(CameraData())
    private val worldStateInternal: MutableLiveData<Boolean> = mutableLiveDataWithDefault(false)


    val recordListForHistoryFlow: Flowable<List<ProcessedWorldRecordForHistory>>
        get() = worldRepository.getRecordListFlow()

    val drawWorldDelegate: DrawWorldDelegate = DrawWorldDelegate(cameraInternal)
    val camera: LiveData<CameraData>
        get() = cameraInternal
    val worldState: LiveData<Boolean>
        get() = worldStateInternal


    override fun onCleared() {
        drawWorldDelegate.onClear()
        worldRepository.closeAndWait()
    }

    private fun generateThumbnail() =
            drawWorldDelegate.generateThumbnail(1000, 1000, camera.value!!.let {
                it.copy(scale = it.scale * 0.5)
            })

    private fun recoverFrom(worldData: WorldData, cameraData: CameraData) {
        world.removeAllBodiesAndJoints()
        worldData.fromData(world)
        drawWorldDelegate.clearAndRegister(world.bodies)
        cameraInternal.value = cameraData
    }

    private fun createNewRecordFromCurrent() {
        worldRepository.createRecord(Date(), world.toData(), camera.value!!, generateThumbnail())
        notifyWorldHistoryScrollToTop()
    }

    private fun updateLatestRecordCamera(cameraData: CameraData) {
        cameraInternal.value = cameraData
        if (!world.isEmpty)
            worldRepository.updateLatestCamera(Date(), cameraData, generateThumbnail())
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
        worldRepaintListeners.forEach {
            it.invoke()
        }
    }

    private fun notifyWorldHistoryScrollToTop() {
        worldHistoryScrollToTopListeners.forEach {
            it.invoke()
        }
    }

    private fun createNewWorld() {
        pauseWorld()
        world.removeAllBodiesAndJoints()
        drawWorldDelegate.unregisterAllBody()
    }

    fun createNewWorldAndResetCamera() {
        createNewWorld()
        cameraInternal.value = CameraData()
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
        drawWorldDelegate.registerBody(body)
        createNewRecordFromCurrent()
        notifyRepaint()
    }

    fun removeBody(body: Body) {
        world.removeBody(body)
        drawWorldDelegate.unregisterBody(body)
        if (!world.isEmpty)
            createNewRecordFromCurrent()
        notifyRepaint()
    }


    fun recoverFromRecordAndUpdateTimestamp(record: ProcessedWorldRecordForHistory) {
        recoverFrom(record.world, record.camera)
        worldRepository.updateTimestamp(record.timestamp, Date())
        notifyWorldHistoryScrollToTop()
        notifyRepaint()
    }

    fun bodyHitTest(x: Double, y: Double): Body? =
            world.bodies.firstOrNull { it.contains(Vector2(x, y)) }

    fun getBodyById(id: UUID) = world.bodies.find { it.id == id }
            ?: throw IllegalArgumentException("No body has id as $id")

    fun updateBody(body: Body) {
        drawWorldDelegate.updateBody(body)
        notifyRepaint()
    }

    private fun CameraData.translateCreate(x: Double, y: Double): CameraData {
        return this.copy(translation = Vector2Data(this.translation.x + x / this.scale, this.translation.y - y / this.scale))
    }

    private fun CameraData.scaleCreate(scale: Double): CameraData {
        return this.copy(scale = this.scale * scale)
    }

    //camera
    fun cameraPostTranslate(x: Float, y: Float) {
        updateLatestRecordCamera(camera.value!!.translateCreate(x.toDouble(), y.toDouble()))
        notifyRepaint()
    }

    fun cameraPostScale(scale: Float) {
        updateLatestRecordCamera(camera.value!!.scaleCreate(scale.toDouble()))
        notifyRepaint()
    }
}
