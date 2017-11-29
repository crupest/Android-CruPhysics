package crupest.cruphysics

import android.graphics.Matrix
import org.dyn4j.dynamics.World
import java.util.*

object WorldManager {
    val viewMatrix = Matrix()
    val world = World()
    val worldStateChangeEvent = Event<WorldStateChangeEventArgs>()

    private val timer = Timer()
    private var runWorldTask: TimerTask? = null

    init {
        viewMatrix.postScale(1.0f, -1.0f)
    }

    var isRunning: Boolean
        get() = runWorldTask != null
        set(value) = if (value) runWorld() else pauseWorld()

    fun runWorld() {
        if (runWorldTask == null) {
            runWorldTask = object : TimerTask() {
                override fun run() {
                    world.step(1)
                }
            }
            worldStateChangeEvent.raise(WorldStateChangeEventArgs(world, true))
            timer.schedule(runWorldTask, 0, (world.settings.stepFrequency * 1000).toLong())
        }
    }

    fun pauseWorld() {
        runWorldTask?.cancel()
        runWorldTask = null
        timer.purge()
        worldStateChangeEvent.raise(WorldStateChangeEventArgs(world, false))
    }
}
