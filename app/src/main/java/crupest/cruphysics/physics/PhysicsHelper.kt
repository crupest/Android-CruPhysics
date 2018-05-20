package crupest.cruphysics.physics

import android.graphics.Canvas
import android.graphics.Matrix
import crupest.cruphysics.utility.toDegrees
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Shape
import org.dyn4j.geometry.Transform

/**
 * Created by crupest on 2017/11/17.
 * Helper functions for physics engine.
 */

fun Canvas.withTransform(transform: Transform, block: (Canvas) -> Unit) {
    val count = this.save()
    this.translate(transform.translationX.toFloat(), transform.translationY.toFloat())
    this.rotate(transform.rotation.toFloat().toDegrees())
    block(this)
    this.restoreToCount(count)
}

fun Body.checkAndGetFixture(): BodyFixture {
    if (this.fixtureCount != 1)
        throw IllegalStateException("The body has 0 or more than 1 fixture.")
    return this.getFixture(0)
}

const val WORLD_VIEW_INIT_SCALE = 500.0f
fun createWorldViewMatrix(): Matrix = Matrix().apply {
    preScale(WORLD_VIEW_INIT_SCALE, -WORLD_VIEW_INIT_SCALE)
}

fun Matrix.resetWorldViewMatrix() {
    reset()
    preScale(WORLD_VIEW_INIT_SCALE, WORLD_VIEW_INIT_SCALE)
}

val Body.cruUserData: BodyUserData
    get() = this.userData as BodyUserData


inline fun Shape.switchShape(circleHandler: (Circle) -> Unit,
                             rectangleHandler: (Rectangle) -> Unit) {
    when (this) {
        is Circle -> circleHandler(this)
        is Rectangle -> rectangleHandler(this)
        else -> throw UnsupportedOperationException(
                "Shape ${this::class.simpleName} is unsupported.")
    }
}

inline fun <T> Shape.switchShapeR(circleHandler: (Circle) -> T,
                                  rectangleHandler: (Rectangle) -> T) = when (this) {
    is Circle -> circleHandler(this)
    is Rectangle -> rectangleHandler(this)
    else -> throw UnsupportedOperationException(
            "Shape ${this::class.simpleName} is unsupported.")
}
