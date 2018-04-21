package crupest.cruphysics.physics

import android.graphics.Matrix
import android.graphics.PointF
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.*

/**
 * Created by crupest on 2017/11/17.
 * Helper functions for physics engine.
 */

fun PointF.toVec2(): Vector2 = Vector2(this.x.toDouble(), this.y.toDouble())

fun Transform.toMatrix(): Matrix {
    val matrix = Matrix()
    matrix.preTranslate(this.translationX.toFloat(), this.translationY.toFloat())
    matrix.preRotate(Math.toDegrees(this.rotation).toFloat())
    return matrix
}

fun Body.checkAndGetFixture(): BodyFixture {
    if (this.fixtureCount != 1)
        throw IllegalStateException("The body has 0 or more than 1 fixture.")
    return this.getFixture(0)
}

fun createWorldViewMatrix(): Matrix = Matrix().apply { preScale(1.0f, -1.0f) }

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
