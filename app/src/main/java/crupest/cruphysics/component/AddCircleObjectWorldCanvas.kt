package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.physics.serialization.CircleData
import crupest.cruphysics.physics.serialization.ShapeData
import crupest.cruphysics.physics.serialization.Vector2Data
import crupest.cruphysics.physics.serialization.createShapeData
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.mapPoint
import crupest.cruphysics.utility.move

/**
 * Created by crupest on 2017/11/6.
 * View component AddCircleObjectWorldCanvas
 */
class AddCircleObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                it.updateMove()
                radiusController.position.move(
                        it.newPosition.x - it.oldPosition.x,
                        it.newPosition.y - it.oldPosition.y
                )
                onControllerMove()
            },
            Controller {
                it.updateMove()
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
        val radius = distance(center, radiusController.position.viewToWorld()).toDouble()

        if (radius == 0.0)
            throw RuntimeException("Circle's radius must be bigger than 0.")

        return CircleData(
                radius = radius
        ).createShapeData() to Vector2Data(center.x.toDouble(), center.y.toDouble())
    }
}
