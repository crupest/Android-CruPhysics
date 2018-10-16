package crupest.cruphysics.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import crupest.cruphysics.CollectionChangedEventArgs
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
                    }
            }


    private val world: World = World()
    private val bodyListChangedListeners: MutableList<(CollectionChangedEventArgs<Body>) -> Unit> = mutableListOf()
    private val worldStepListeners: MutableList<() -> Unit> = mutableListOf()
    private val worldStateChangedListeners: MutableList<(Boolean) -> Unit> = mutableListOf()

    private var thumbnailDelegateList: MutableList<((Int, Int, CameraData) -> Bitmap)> = mutableListOf()

    private var task: ScheduleTask? = null

    val camera: MutableLiveData<CameraData> = MutableLiveData()
    val recordList: LiveData<PagedList<WorldRecordEntity>> =
            LivePagedListBuilder(
                    worldRepository.dao.getRecords(),
                    PagedList.Config.Builder().setPageSize(20).setEnablePlaceholders(false).build()
            ).build()

    init {
        camera.value = CameraData()
        camera.observeForever {
            if (!world.isEmpty)
                generateThumbnail().apply {
                    if (this == null)
                        Log.d("MainViewModel", "Failed to generate thumbnail of world when camera is changed.")
                    else
                        worldRepository.updateLatestRecordCamera(it, this)
                }
        }
    }

    override fun onCleared() {
        worldRepository.closeAndWait()
    }

    private fun generateThumbnail() = thumbnailDelegateList.lastOrNull()?.invoke(1000, 500, camera.value!!.apply {
        CameraData(this.translation, this.scale * 0.5)
    })

    fun recoverFromRecord(worldRecord: WorldRecordEntity) {
        val previousBodyList = world.bodies.toList()
        world.removeAllBodiesAndJoints()
        bodyListChangedListeners.forEach {
            it.invoke(CollectionChangedEventArgs(null, previousBodyList))
        }
        worldRecord.world.fromJson<WorldData>().fromData(world)
        camera.value = worldRecord.camera.fromJson()
    }

    private fun createCurrentRecord() {
        worldRepository.addRecord(world.toData(), camera.value!!, generateThumbnail()
                ?: throw IllegalStateException("Failed to generate thumbnail of world when create new record."))
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

    fun registerBodyListChangedListener(lifecycleOwner: LifecycleOwner, listener: (CollectionChangedEventArgs<Body>) -> Unit) {
        registerListener(lifecycleOwner, bodyListChangedListeners, listener)
    }

    fun registerWorldStepListener(lifecycleOwner: LifecycleOwner, listener: () -> Unit) {
        registerListener(lifecycleOwner, worldStepListeners, listener)
    }

    fun registerWorldStateChangedListener(lifecycleOwner: LifecycleOwner, listener: (Boolean) -> Unit) {
        registerListener(lifecycleOwner, worldStateChangedListeners, listener)
    }

    fun registerThumbnailDelegate(lifecycleOwner: LifecycleOwner, delegate: (Int, Int, CameraData) -> Bitmap) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun addListener() {
                thumbnailDelegateList.add(delegate)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun removeListener() {
                thumbnailDelegateList.remove(delegate)
            }
        })
    }

    private fun createNewWorld() {
        pauseWorld()
        val previousBodyList = world.bodies.toList()
        world.removeAllBodiesAndJoints()
        bodyListChangedListeners.forEach {
            it.invoke(CollectionChangedEventArgs(null, previousBodyList))
        }
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
                    it.invoke(true)
                }
                createCurrentRecord()
                true
            } else false

    fun addBody(body: Body) {
        world.addBody(body)
        bodyListChangedListeners.forEach {
            it.invoke(CollectionChangedEventArgs(listOf(body), null))
        }
        createCurrentRecord()
    }

    fun removeBody(body: Body) {
        world.removeBody(body)
        bodyListChangedListeners.forEach {
            it.invoke(CollectionChangedEventArgs(null, listOf(body)))
        }
        createCurrentRecord()
    }

    fun bodyHitTest(x: Double, y: Double): Body? =
            world.bodies.firstOrNull { it.contains(Vector2(x, y)) }
}
