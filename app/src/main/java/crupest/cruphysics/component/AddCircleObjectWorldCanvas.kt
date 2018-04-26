package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import crupest.cruphysics.physics.serialization.*
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.mapPoint

/**
 * Created by crupest on 2017/11/6.
 * View component AddCircleObjectWorldCanvas
 */
class AddCircleObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                radiusController.position.set(
                        radiusController.position.x + (it.newPosition.x - it.oldPosition.x),
                        radiusController.position.y + (it.newPosition.y - it.oldPosition.y)
                )
                onControllerMove()
            },
            Controller {
                onControllerMove()
            }
    )

    private val centerController: Controller
        get() = controllers[0]

    private val radiusController: Controller
        get() = controllers[1]

    private fun onControllerMove() {
        repaint()
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        canvas.drawCircle(
                centerController.position.x,
                centerController.position.y,
                distance(centerController.position, radiusController.position),
                objectPaint,
                objectBorderPaint
        )

        drawControllers(canvas)
    }

    override fun reset() {
        val centerX = width.toFloat() / 2.0f
        val centerY = height.toFloat() / 2.0f

        centerController.position.set(centerX, centerY)
        radiusController.position.set(centerX + 300.0f, centerY)

        onControllerMove()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        centerController.position.set(matrix.mapPoint(centerController.position))
        radiusController.position.set(matrix.mapPoint(radiusController.position))
        onControllerMove()
    }

    override fun generateShapeAndPosition(): Pair<ShapeData, Vector2Data> {
        val center = centerController.position.viewToWorld()
        return CircleData(
                radius = distance(center, radiusController.position.viewToWorld()).toDouble()
        ).createShapeData() to Vector2Data(center.x.toDouble(), center.y.toDouble())
    }
}
