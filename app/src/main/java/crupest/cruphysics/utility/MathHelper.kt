package crupest.cruphysics.utility

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
        sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))

fun Float.toDegrees(): Float = this * 180.0f / PI.toFloat()
