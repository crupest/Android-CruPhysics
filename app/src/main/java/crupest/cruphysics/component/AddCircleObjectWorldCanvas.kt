package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.physics.serialization.CircleData
import crupest.cruphysics.physics.serialization.Vector2Data
import crupest.cruphysics.physics.serialization.createShapeData
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.mapPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by crupest on 2017/11/6.
 * View component AddCircleObjectWorldCanvas
 */
class AddCircleObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                centerX = it.position.x
                centerY = it.position.y
                updateControllerPosition()
                repaint()
            },
            Controller {
                radius = distance(it.position.x, centerX, it.position.y, centerY)
                angle = atan2(it.position.y - centerY, it.position.x - centerX)
                updateControllerPosition()
                repaint()
            }
    )

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var radius: Float = 300.0f
    private var angle: Float = 0.0f //in radium

    private val centerController: Controller
        get() = controllers[0]

    private val radiusController: Controller
        get() = controllers[1]

    private fun updateControllerPosition() {
        centerController.position.set(centerX, centerY)
        radiusController.position.set(
                centerX + radius * cos(radius),
                centerY + radius * sin(radius)
        )
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        //Don't need to rotate.
        canvas.drawCircle(
                centerX,
                centerY,
                radius,
                objectPaint,
                objectBorderPaint
        )

        drawControllers(canvas)
    }

    override fun initialize() {
        centerX = width.toFloat() / 2.0f
        centerY = height.toFloat() / 2.0f

        updateControllerPosition()
        repaint()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        val newCenter = matrix.mapPoint(centerX, centerY)
        centerX = newCenter.x
        centerY = newCenter.y

        radius = matrix.mapRadius(radius)

        updateControllerPosition()
        repaint()
    }

    override fun generateShapeInfo(): ShapeInfo {
        val center = centerController.position.viewToWorld()
        val radius = distance(center, radiusController.position.viewToWorld()).toDouble()

        if (radius == 0.0)
            throw RuntimeException("Circle's radius must be bigger than 0.")

        return ShapeInfo(
                CircleData(radius = radius).createShapeData(),
                Vector2Data(center.x.toDouble(), center.y.toDouble()),
                angle.toDouble()
        )
    }
}
