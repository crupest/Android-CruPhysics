package crupest.cruphysics

/**
 * Created by crupest on 2017/11/22.
 * A rect in double-precision
 */
class MyRectF(
        var left: Float = 0.0f,
        var top: Float = 0.0f,
        var right: Float = 0.0f,
        var bottom: Float = 0.0f) {

    val width: Float
        get() = right - left

    val height: Float
        get() = top - bottom

    val centerX: Float
        get() = (left + right) / 2.0f

    val centerY: Float
        get() = (top + bottom) / 2.0f
}
