package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import crupest.cruphysics.physics.toVec2
import crupest.cruphysics.utility.mapPoint
import org.dyn4j.geometry.Polygon
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by crupest on 2017/12/1.
 * View component AddPolygonObjectWorldCanvas
 */

class AddPolygonObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    private val path = Path()
    private val objectPaint = Paint()
    private val objectBorderPaint = Paint()

    override var controllers: Array<Controller> = Array(3) {
        Controller {
            onControllerMove()
        }
    }

    init {
        objectPaint.color = Color.BLUE
        objectBorderPaint.style = Paint.Style.STROKE
        objectBorderPaint.color = Color.BLACK
        objectBorderPaint.strokeWidth = 3.0f
    }

    override fun reset() {
        val centerX = width.toFloat() / 2.0f
        val centerY = height.toFloat() / 2.0f

        val polygon = generateRegularPolygonVertices(sideCount, center = PointF(centerX, centerY))
        for (i in 0 until sideCount) {
            controllers[i].position.set(polygon[i])
        }

        onControllerMove()
    }

    private fun onControllerMove() {
        calculateShapeFromControllers()
        invalidate()
    }

    private fun calculateShapeFromControllers() {
        path.reset()

        path.moveTo(controllers[0].position.x, controllers[0].position.y)
        for (i in 1 until sideCount) {
            path.lineTo(controllers[i].position.x, controllers[i].position.y)
        }
        path.lineTo(controllers[0].position.x, controllers[0].position.y)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawPath(path, objectPaint)
        canvas.drawPath(path, objectBorderPaint)

        drawControllers(canvas)
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        for (controller in controllers) {
            controller.position.set(matrix.mapPoint(controller.position))
        }
        onControllerMove()
    }

    private fun generateRegularPolygonVertices(sideCount: Int, center: PointF = PointF(), radius: Float = 300.0f): List<PointF> {
        val angle = PI.toFloat() * 2.0f / sideCount.toFloat()
        val result = List(sideCount) {
            PointF()
        }

        for (i in 0 until sideCount) {
            result[i].set(
                    center.x + radius * cos(angle * i),
                    center.y + radius * sin(angle * i)
            )
        }

        return result
    }

    var sideCount: Int
        get() = controllers.size
        set(value) {
            controllers = Array(value) {
                Controller {
                    onControllerMove()
                }
            }
            reset()
        }

    /**
     * @throws IllegalArgumentException throws if the polygon isn't a convex
     */
    fun generatePolygon(): Polygon = Polygon(*Array(sideCount) {
        viewToWorld(controllers[it].position).toVec2()
    }.reversedArray())

    var color: Int
        get() = objectPaint.color
        set(value) {
            objectPaint.color = value
            invalidate()
        }
}
