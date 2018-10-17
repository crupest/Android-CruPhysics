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
import crupest.cruphysics.utility.postOnMainThread
import crupest.cruphysics.utility.setInterval
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.Vector2

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val worldRepository: WorldRepository =
            WorldRepository(getApplication<Application>().applicationContext) { record ->
                if (record != null)
                    postOnMainThread {
                        recoverFromRecord(record)
                        camera.observeForever(this::updateLatestRecordCamera)
                    }
            }

    private var task: ScheduleTask? = null

    private val world: World = World()
    private val worldStepListeners: MutableList<() -> Unit> = mutableListOf()
    private val worldStateChangedListeners: MutableList<(Boolean) -> Unit> = mutableListOf()

    private val drawWorldDelegateInternal: MutableLiveData<WorldCanvasDelegate> = MutableLiveData()

    private var updateCamera = false

    val camera: MutableLiveData<CameraData> = MutableLiveData()
    val recordList: LiveData<List<WorldRecordEntity>> = worldRepository.records

    val drawWorldDelegate: LiveData<IDrawDelegate> = Transformations.map(drawWorldDelegateInternal) { it }


    init {
        drawWorldDelegateInternal.value = WorldCanvasDelegate()
    }

    override fun onCleared() {
        worldRepository.closeAndWait()
    }

    private fun generateThumbnail() =
            drawWorldDelegateInternal.value!!.generateThumbnail(1000, 1000, camera.value!!.let {
                it.copy(scale = it.scale * 0.6)
            })

    private fun updateLatestRecordCamera(cameraData: CameraData) {
        val list = recordList.value
        if (updateCamera && !world.isEmpty && list != null && !list.isEmpty())
            worldRepository.updateRecordCamera(list[0], cameraData, generateThumbnail())
    }


    fun recoverFromRecord(worldRecord: WorldRecordEntity) {
        world.removeAllBodiesAndJoints()
        worldRecord.world.fromJson<WorldData>().fromData(world)
        drawWorldDelegateInternal.value = WorldCanvasDelegate(world.bodies)
        updateCamera = false
        camera.value = worldRecord.camera.fromJson()
        updateCamera = true
    }

    private fun createCurrentRecord() {
        worldRepository.addRecord(world.toData(), camera.value!!, generateThumbnail())
    }

    fun updateTimestamp(worldRecord: WorldRecordEntity) {
        worldRepository.updateTimestamp(worldRecord)
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

    fun registerWorldStateChangedListener(lifecycleOwner: LifecycleOwner, listener: (Boolean) -> Unit) {
        registerListener(lifecycleOwner, worldStateChangedListeners, listener)
    }

    private fun createNewWorld() {
        pauseWorld()
        world.removeAllBodiesAndJoints()
        drawWorldDelegateInternal.value = WorldCanvasDelegate()
    }

    fun createNewWorldAndResetCamera() {
        createNewWorld()
        camera.value = CameraData()
    }

    fun runWorld(): Boolean =
            if (task == null) {
                task = setInterval(1.0 / 60.0) {
                    world.update(1.0 / 60.0)
                    worldStepListeners.forEach {
                        it.invoke()
                    }
                }
                worldStateChangedListeners.forEach {
                    it.invoke(true)
                }
                true
            } else false

    fun pauseWorld() =
            if (task != null) {
                task!!.cancel()
                task = null
                worldStateChangedListeners.forEach {
                    it.invoke(false)
                }
                createCurrentRecord()
                true
            } else false

    fun addBody(body: Body) {
        world.addBody(body)
        drawWorldDelegateInternal.value!!.registerBody(body)
        createCurrentRecord()
    }

    fun removeBody(body: Body) {
        world.removeBody(body)
        drawWorldDelegateInternal.value!!.unregisterBody(body)
        createCurrentRecord()
    }

    fun bodyHitTest(x: Double, y: Double): Body? =
            world.bodies.firstOrNull { it.contains(Vector2(x, y)) }
}
