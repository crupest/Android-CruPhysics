package crupest.cruphysics.utility

import java.util.*

/**
 * Created by crupest on 2017/11/26.
 * Functions related with timers.
 */

private object TimerPool {
    val timerList = mutableListOf<Timer>()
}

interface ScheduleTask {
    fun cancel()
}

fun setTimeout(seconds: Double, task: () -> Unit) : ScheduleTask {
    return object : ScheduleTask {
        private val timer = Timer()
        private val timerTask = object : TimerTask() {
            override fun run() {
                task()
                clean()
            }
        }

        init {
            TimerPool.timerList.add(timer)
            timer.schedule(timerTask, (seconds * 1000.0).toLong())
        }

        override fun cancel() {
            clean()
        }

        private fun clean() {
            timer.cancel()
            TimerPool.timerList.remove(timer)
        }
    }
}

fun setInterval(seconds: Double, task: () -> Unit) : ScheduleTask =
        setInterval(seconds, true, task)

fun setInterval(seconds: Double, runNow: Boolean, task: () -> Unit) : ScheduleTask {
    return object : ScheduleTask {
        private val timer = Timer()
        private val timerTask = object : TimerTask() {
            override fun run() {
                task()
            }
        }

        init {
            TimerPool.timerList.add(timer)
            val interval = (seconds * 1000.0).toLong()
            timer.schedule(timerTask, if (runNow) 0 else interval, interval)
        }

        override fun cancel() {
            clean()
        }

        private fun clean() {
            timer.cancel()
            TimerPool.timerList.remove(timer)
        }
    }
}
