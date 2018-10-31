package crupest.cruphysics.physics

import android.graphics.Canvas
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

fun Canvas.withTransform(transform: Transform, block: Canvas.() -> Unit) {
    val count = this.save()
    this.translate(transform.translationX.toFloat(), transform.translationY.toFloat())
    this.rotate(transform.rotation.toFloat().toDegrees())
    this.block()
    this.restoreToCount(count)
}

fun Body.checkAndGetFixture(): BodyFixture {
    if (this.fixtureCount != 1)
        throw IllegalStateException("The body has 0 or more than 1 fixture.")
    return this.getFixture(0)
}

val Body.cruUserData: BodyUserData
    get() = this.userData as BodyUserData


class ShapeHandlers<TReturn>(throwOnElse: Boolean) {

    var circleHandler: ((Circle) -> TReturn)? = null
    var rectangleHandler: ((Rectangle) -> TReturn)? = null
    var elseHandler: (() -> TReturn)? = null

    init {
        if (throwOnElse)
            onElse { throw IllegalStateException("Unknown shape.") }
    }

    fun onCircle(block: (Circle) -> TReturn) {
        check(circleHandler == null)
        circleHandler = block
    }

    fun onRectangle(block: (Rectangle) -> TReturn) {
        check(rectangleHandler == null)
        rectangleHandler = block
    }

    fun onElse(block: () -> TReturn) {
        check(elseHandler == null)
        elseHandler = block
    }

    fun check() {
        checkNotNull(circleHandler)
        checkNotNull(rectangleHandler)
        checkNotNull(elseHandler)
    }
}

inline fun Shape.switchShape(throwOnElse: Boolean = true, block: ShapeHandlers<Unit>.() -> Unit) {
    val handlers = ShapeHandlers<Unit>(throwOnElse).apply(block)
    handlers.check()
    when (this) {
        is Circle -> handlers.circleHandler!!.invoke(this)
        is Rectangle -> handlers.rectangleHandler!!.invoke(this)
        else -> handlers.elseHandler!!.invoke()
    }
}

inline fun <T> Shape.switchShapeR(throwOnElse: Boolean = true, block: ShapeHandlers<T>.() -> Unit): T {
    val handlers = ShapeHandlers<T>(throwOnElse).apply(block)
    handlers.check()
    return when (this) {
        is Circle -> handlers.circleHandler!!.invoke(this)
        is Rectangle -> handlers.rectangleHandler!!.invoke(this)
        else -> handlers.elseHandler!!.invoke()
    }
}
