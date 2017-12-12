package crupest.cruphysics.physics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import crupest.cruphysics.physics.serialization.JsonObject
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Polygon

/**
 * Created by crupest on 2017/12/4.
 * Class [PolygonBodyUserData].
 */
class PolygonBodyUserData(override val body: Body, color: Int = Color.BLUE) : BodyUserData {

    private val path = Path()
    private val paint = Paint()

    init {
        paint.color = color

        val shape = body.fixtures[0].shape
        if (shape is Polygon) {
            path.moveTo(shape.vertices[0].x.toFloat(), shape.vertices[0].y.toFloat())
            for (i in 1 until shape.vertices.size) {
                path.lineTo(shape.vertices[i].x.toFloat(), shape.vertices[i].y.toFloat())
            }
            path.lineTo(shape.vertices[0].x.toFloat(), shape.vertices[0].y.toFloat())
        } else {
            throw UnsupportedOperationException("PolygonBodyUserData's related body is not a circle.")
        }
    }


    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(body.transform.toMatrix())
        canvas.drawPath(path, paint)
        canvas.restore()
    }

    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }

    override fun toJsonObject(): JsonObject {
        TODO("not implemented")
    }
}
