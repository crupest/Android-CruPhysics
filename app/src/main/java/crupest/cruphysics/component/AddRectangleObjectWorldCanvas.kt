package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import crupest.cruphysics.physics.serialization.RectangleData
import crupest.cruphysics.physics.serialization.ShapeData
import crupest.cruphysics.physics.serialization.Vector2Data
import crupest.cruphysics.physics.serialization.createShapeData
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.mapPoint

/**
 * Created by crupest on 2017/11/17.
 * View component AddRectangleObjectWorldCanvas
 */
class AddRectangleObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                sizeController.position.set(
                        sizeController.position.x + (it.newPosition.x - it.oldPosition.x),
                        sizeController.position.y + (it.newPosition.y - it.oldPosition.y)
                )
                onControllerMove()
            },
            Controller {
                sizeController.position.set(
                        maxOf(positionController.position.x, it.newPosition.x),
                        maxOf(positionController.position.y, it.newPosition.y)
                )
                onControllerMove()
            }
    )

    private val positionController
        get() = controllers[0]

    private val sizeController
        get() = controllers[1]

    private fun onControllerMove() {
        repaint()
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        canvas.drawRectangle(
                positionController.position.x,
                positionController.position.y,
                sizeController.position.x,
                sizeController.position.y,
                objectPaint,
                objectBorderPaint
        )

        drawControllers(canvas)
    }

    override fun reset() {
        val centerX = width.toFloat() / 2.0f
        val centerY = height.toFloat() / 2.0f

        positionController.position.set(centerX - 300.0f, centerY - 200.0f)
        sizeController.position.set(centerX + 300.0f, centerY + 200.0f)

        onControllerMove()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        positionController.position.set(matrix.mapPoint(positionController.position))
        sizeController.position.set(matrix.mapPoint(sizeController.position))
        onControllerMove()
    }

    override fun generateShapeAndPosition(): Pair<ShapeData, Vector2Data> {
        val leftTop = positionController.position.viewToWorld()
        val rightBottom = sizeController.position.viewToWorld()
        val width = (rightBottom.x - leftTop.x).toDouble()
        val height = (leftTop.y - rightBottom.y).toDouble()

        if (width == 0.0)
            throw RuntimeException("Rectangle's width must be bigger than 0.")
        if (height == 0.0)
            throw RuntimeException("Rectangle's height must be bigger than 0.")

        return RectangleData(
                width = width,
                height = height
        ).createShapeData() to Vector2Data(
                x = (leftTop.x + rightBottom.x).toDouble() / 2.0,
                y = (leftTop.y + rightBottom.y).toDouble() / 2.0
        )
    }
}
