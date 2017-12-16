package crupest.cruphysics.physics

import android.graphics.Matrix
import crupest.cruphysics.Event
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.physics.serialization.mapper
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setInterval
import crupest.cruphysics.utility.values
import org.dyn4j.dynamics.World

object WorldManager {
    val viewMatrix = Matrix()
    val world = World()
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
            "view_matrix" to viewMatrix.values,
            "world" to mapper.map(world)
    )
}
