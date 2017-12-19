package crupest.cruphysics.physics

import android.graphics.Matrix
import crupest.cruphysics.Event
import crupest.cruphysics.physics.serialization.*
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import org.dyn4j.dynamics.World
import java.io.File

object WorldManager {
    val viewMatrix = Matrix()
    var world = World()
        private set(value) {
            field = value
        }
    val worldChangeEvent = Event<WorldChangeEventArgs>()
    val worldStateChangeEvent = Event<WorldStateChangeEventArgs>()

    private var task: ScheduleTask? = null

    init {
        viewMatrix.postScale(1.0f, -1.0f)
    }

    fun runWorld() {
        if (task == null) {
            task = setInterval(world.settings.stepFrequency) {
                world.step(1)
            }
            worldStateChangeEvent.raise(WorldStateChangeEventArgs(world, true))
        }
    }

    fun pauseWorld() {
        if (task != null) {
            task!!.cancel()
            task = null
            worldStateChangeEvent.raise(WorldStateChangeEventArgs(world, false))
        }
    }

    fun toJsonObject(): JsonObject = mapOf(
            "version" to "1.0",
            "view_matrix" to mapper.map(viewMatrix),
            "world" to mapper.map(world)
    )

    fun fromJsonObject(obj: JsonObject) {
        if (obj.getStringProperty("version") == "1.0") {
            val oldWorld = world
            world = unmapper.unmapWorld(obj.getObjectProperty("world"))
            worldChangeEvent.raise(WorldChangeEventArgs(oldWorld, world))
            viewMatrix.set(unmapper.unmapMatrix(obj.getArrayProperty("view_matrix")))
        } else {
            throw RuntimeException("Unknown version.")
        }
    }

    fun saveToFile(file: File) {
        file.writeText(objectAdapter.toJson(toJsonObject()))
    }

    fun readFromFile(file: File) {
        fromJsonObject(objectAdapter.fromJson(file.readText())!!)
    }

    fun createNewWorld() {
        val oldWorld = world
        world = World()
        resetView()
        worldChangeEvent.raise(WorldChangeEventArgs(oldWorld, world))
    }

    fun resetView() {
        viewMatrix.reset()
        viewMatrix.postScale(1.0f, -1.0f)
    }
}
