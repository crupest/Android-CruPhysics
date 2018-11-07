package crupest.cruphysics.viewmodel

import android.graphics.Canvas
import android.graphics.Color
import crupest.cruphysics.component.delegate.IDrawDelegate
import crupest.cruphysics.physics.checkAndGetFixture
import crupest.cruphysics.physics.cruUserData
import crupest.cruphysics.physics.switchShape
import crupest.cruphysics.physics.view.BodyViewData
import crupest.cruphysics.physics.withTransform
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.strokePaint
import org.dyn4j.dynamics.Body

class DrawWorldDelegate : IDrawDelegate {
    companion object {
        private const val BORDER_WIDTH = 8.0f
    }

    private val bodyViewDataMap: MutableMap<Body, BodyViewData> = mutableMapOf()

    private val bodyBorderPaint = strokePaint(Color.BLACK)

    fun registerBody(body: Body) {
        if (body in bodyViewDataMap)
            throw IllegalArgumentException("The body has already been registered.")
        bodyViewDataMap[body] = BodyViewData(body.cruUserData.color)
    }

    fun unregisterBody(body: Body) {
        if (body !in bodyViewDataMap)
            throw IllegalArgumentException("The body hasn't been registered.")
        bodyViewDataMap.remove(body)
    }

    fun unregisterAllBody() {
        bodyViewDataMap.clear()
    }

    fun updateBody(body: Body) {
        val viewData = bodyViewDataMap[body] ?: throw IllegalArgumentException("The body hasn't been registered.")
        if (body.cruUserData.color != viewData.color)
            viewData.color = body.cruUserData.color
    }

    fun registerBodies(bodies: Iterable<Body>) {
        bodies.forEach {
            registerBody(it)
        }
    }

    fun clearAndRegister(bodies: Iterable<Body>) {
        unregisterAllBody()
        registerBodies(bodies)
    }

    fun setScale(scale: Double) {
        bodyBorderPaint.strokeWidth = BORDER_WIDTH / scale.toFloat()
    }

    override fun draw(canvas: Canvas) {
        for ((body, bodyViewData) in bodyViewDataMap) {
            val shape = body.checkAndGetFixture().shape
            canvas.withTransform(body.transform) {
                shape.switchShape {
                    onCircle {
                        drawCircle(
                                it.center.x.toFloat(),
                                it.center.y.toFloat(),
                                it.radius.toFloat(),
                                bodyViewData.paint,
                                bodyBorderPaint
                        )
                    }
                    onRectangle {
                        val hw = it.width / 2.0
                        val hh = it.height / 2.0
                        drawRectangle(
                                (it.center.x - hw).toFloat(),
                                (it.center.y + hh).toFloat(),
                                (it.center.x + hw).toFloat(),
                                (it.center.y - hh).toFloat(),
                                bodyViewData.paint,
                                bodyBorderPaint
                        )
                    }
                }
            }
        }
    }

    fun onClear() {
        unregisterAllBody()
    }
}
