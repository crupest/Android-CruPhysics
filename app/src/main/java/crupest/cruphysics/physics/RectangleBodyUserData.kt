package crupest.cruphysics.physics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.physics.serialization.mapper
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Rectangle


/**
 * Created by crupest on 2017/11/25.
 * Class RectangleBodyUserData.
 */
class RectangleBodyUserData(body: Body, color: Int = Color.BLUE) : BodyUserData(body) {

    private val paint = Paint()

    init {
        paint.color = color
    }

    override fun draw(canvas: Canvas) {
        val shape = body.fixtures[0].shape
        if (shape is Rectangle) {
            canvas.save()
            canvas.concat(body.transform.toMatrix())
            val hw = shape.width / 2.0
            val hh = shape.height / 2.0
            canvas.drawRect(
                    (shape.center.x - hw).toFloat(),
                    (shape.center.y + hh).toFloat(),
                    (shape.center.x + hw).toFloat(),
                    (shape.center.y - hh).toFloat(),
                    paint
            )
            canvas.restore()
        } else {
            throw UnsupportedOperationException("RectangleBodyUserData's related body is not a rectangle.")
        }
    }

    override var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }

    override fun toJsonObject(): JsonObject {
        val shape = body.fixtures[0]
        if (shape is Rectangle) {
            return basePropertyToJsonObject().plus(
                    "shape" to mapper.map(shape)
            )
        } else {
            throw UnsupportedOperationException("RectangleBodyUserData's related body is not a rectangle.")
        }
    }
}
