package crupest.cruphysics.physics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.physics.serialization.putBodyFixtureProperty
import crupest.cruphysics.physics.serialization.toJsonObject
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle

/**
 * Created by crupest on 2017/11/24.
 * Class CircleBodyUserData.
 */

class CircleBodyUserData(override val body: Body, color: Int = Color.BLUE) : BodyUserData {

    private val paint = Paint()

    init {
        paint.color = color
    }


    override fun draw(canvas: Canvas) {
        val shape = body.fixtures[0].shape
        if (shape is Circle) {
            canvas.save()
            canvas.concat(body.transform.toMatrix())
            canvas.drawCircle(
                    shape.center.x.toFloat(),
                    shape.center.y.toFloat(),
                    shape.radius.toFloat(),
                    paint
            )
            canvas.restore()
        } else {
            throw UnsupportedOperationException("CircleBodyUserData's related body is not a circle.")
        }
    }

    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }

    override fun toJsonObject(): JsonObject {
        val result = mutableMapOf<String, Any>()
        val fixture = body.fixtures[0]
        fixture.putBodyFixtureProperty(result)
        val shape = fixture.shape
        if (shape is Circle) {
            result.put("shape", mapOf(
                    "type" to "circle",
                    "center" to shape.center.toJsonObject(),
                    "radius" to shape.radius
            ))
        } else {
            throw UnsupportedOperationException("CircleBodyUserData's related body is not a circle.")
        }
        return result
    }
}
