package crupest.cruphysics.physics.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import crupest.cruphysics.component.IDrawWorldDelegate
import crupest.cruphysics.physics.checkAndGetFixture
import crupest.cruphysics.physics.cruUserData
import crupest.cruphysics.physics.switchShape
import crupest.cruphysics.physics.toMatrix
import org.dyn4j.dynamics.Body

class WorldViewData() : IDrawWorldDelegate {
    constructor(bodies: Iterable<Body>) : this() {
        for (body in bodies)
            registerBody(body)
    }

    private val bodyViewDataMap: MutableMap<Body, BodyViewData> = mutableMapOf()

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

    operator fun get(body: Body): BodyViewData {
        return bodyViewDataMap[body]
                ?: throw IllegalArgumentException("The body hasn't been registered.")
    }

    override fun draw(canvas: Canvas) {
        for ((body, bodyViewData) in bodyViewDataMap) {
            val shape = body.checkAndGetFixture().shape

            canvas.save()
            canvas.concat(body.transform.toMatrix())

            shape.switchShape(
                    circleHandler = {
                        canvas.drawCircle(
                                it.center.x.toFloat(),
                                it.center.y.toFloat(),
                                it.radius.toFloat(),
                                bodyViewData.paint
                        )
                    },
                    rectangleHandler = {
                        val hw = it.width / 2.0
                        val hh = it.height / 2.0
                        canvas.drawRect(
                                (it.center.x - hw).toFloat(),
                                (it.center.y + hh).toFloat(),
                                (it.center.x + hw).toFloat(),
                                (it.center.y - hh).toFloat(),
                                bodyViewData.paint
                        )
                    }
            )

            canvas.restore()
        }
    }

    fun generateThumbnail(viewMatrix: Matrix): Bitmap {
        val bitmap = Bitmap.createBitmap(1000, 500, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).also {
            it.concat(viewMatrix)
            draw(it)
        }
        return bitmap
    }
}
