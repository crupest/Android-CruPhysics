package crupest.cruphysics.viewmodel

import android.app.Application
import androidx.lifecycle.*
import crupest.cruphysics.component.delegate.IDrawDelegate
import crupest.cruphysics.component.delegate.WorldCanvasDelegate
import crupest.cruphysics.data.world.WorldRecordEntity
import crupest.cruphysics.data.world.WorldRepository
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.fromData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.serialization.toData
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.nowLong
import crupest.cruphysics.utility.postOnMainThread
import crupest.cruphysics.utility.setInterval
import io.reactivex.Flowable
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2
import org.reactivestreams.Subscriber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val worldRepository: WorldRepository =
            WorldRepository(getApplication<Application>().applicationContext) { record ->
                if (record != null)
                    postOnMainThread {
                        recoverFromRecord(record)
                    }
            }

    private var task: ScheduleTask? = null


    private var currentCameraUpdateFlow: Subscriber<WorldRepository.UpdateCameraInfo>? = null

    private val world: World = World()
    private val worldStepListeners: MutableList<() -> Unit> = mutableListOf()

    private val drawWorldDelegateInternal: MutableLiveData<WorldCanvasDelegate> = MutableLiveData()
    private val cameraInternal: MutableLiveData<CameraData> = MutableLiveData()
    private val worldStateInternal: MutableLiveData<Boolean> = MutableLiveData()


    val recordList: Flowable<List<WorldRecordEntity>> = worldRepository.records

    val camera: LiveData<CameraData>
        get() = cameraInternal
    val drawWorldDelegate: LiveData<IDrawDelegate> = Transformations.map(drawWorldDelegateInternal) { it }
    val worldState: LiveData<Boolean>
        get() = worldStateInternal

    init {
        drawWorldDelegateInternal.value = WorldCanvasDelegate()
        worldStateInternal.value = false
    }

    override fun onCleared() {
        worldRepository.closeAndWait()
    }

    private fun generateThumbnail() =
            drawWorldDelegateInternal.value!!.generateThumbnail(1000, 1000, camera.value!!.let {
                it.copy(scale = it.scale * 0.6)
            })

    private fun createNewCameraUpdateFlow(timestamp: Long) {
        currentCameraUpdateFlow = worldRepository.createNewCameraUpdateFlow(timestamp)
    }

    private fun recoverFrom(worldData: String, cameraData: String) {
        world.removeAllBodiesAndJoints()
        worldData.fromJson<WorldData>().fromData(world)
        drawWorldDelegateInternal.value = WorldCanvasDelegate(world.bodies)
        cameraInternal.value = cameraData.fromJson<CameraData>()
    }

    private fun recoverFromRecord(record: WorldRecordEntity) {
        recoverFrom(record.world, record.camera)
        createNewCameraUpdateFlow(record.timestamp)
    }

    private fun createNewRecordFromCurrent() {
        val timestamp = nowLong()
        worldRepository.createRecord(timestamp, world.toData(), camera.value!!, generateThumbnail())
        createNewCameraUpdateFlow(timestamp)
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

    fun registerWorldStepListener(lifecycleOwner: LifecycleOwner, listener: () -> Unit) {
        registerListener(lifecycleOwner, worldStepListeners, listener)
    }

    private fun createNewWorld() {
        pauseWorld()
        world.removeAllBodiesAndJoints()
        drawWorldDelegateInternal.value = WorldCanvasDelegate()
    }

    fun createNewWorldAndResetCamera() {
        createNewWorld()
        cameraInternal.value = CameraData()
    }

    fun runWorld(): Boolean =
            if (task == null) {
                task = setInterval(1.0 / 60.0) {
                    world.update(1.0 / 60.0)
                    worldStepListeners.forEach {
                        it.invoke()
                    }
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
        drawWorldDelegateInternal.value!!.registerBody(body)
        createNewRecordFromCurrent()
    }

    fun removeBody(body: Body) {
        world.removeBody(body)
        drawWorldDelegateInternal.value!!.unregisterBody(body)
        createNewRecordFromCurrent()
    }

    fun updateLatestRecordCamera(cameraData: CameraData) {
        cameraInternal.value = cameraData
        if (world.isEmpty)
            currentCameraUpdateFlow?.onNext(
                    WorldRepository.UpdateCameraInfo(cameraData, generateThumbnail())
            )
    }

    fun recoverFromRecordAndUpdateTimestamp(record: WorldRecordEntity) {
        recoverFrom(record.world, record.camera)
        val timestamp = nowLong()
        worldRepository.updateTimestamp(record.id, timestamp)
        createNewCameraUpdateFlow(timestamp)
    }

    fun bodyHitTest(x: Double, y: Double): Body? =
            world.bodies.firstOrNull { it.contains(Vector2(x, y)) }
}
