package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.mapPoint
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Vector2

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
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawRectangle(
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

    override fun generateShapeAndPosition(): ShapeAndPosition {
        val leftTop = positionController.position.viewToWorld()
        val rightBottom = sizeController.position.viewToWorld()
        return ShapeAndPosition(
                Rectangle(
                        (rightBottom.x - leftTop.x).toDouble(),
                        (leftTop.y - rightBottom.y).toDouble()
                ),
                Vector2(
                        (leftTop.x + rightBottom.x).toDouble() / 2.0,
                        (leftTop.y + rightBottom.y).toDouble() / 2.0
                )
        )
    }
}
