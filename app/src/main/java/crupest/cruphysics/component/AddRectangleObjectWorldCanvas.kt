package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
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
                centerX = it.position.x
                centerY = it.position.y
                updateControllerPosition()
                repaint()
            },
            Controller {
                //TODO: Calculate the foot from "position" to diagonal
                updateControllerPosition()
                repaint()
            }
    )

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var rWidth: Float = 0.0f
    private var rHeight: Float = 0.0f
    private var angle: Float = 0.0f

    private val centerController
        get() = controllers[0]

    private val sizeController
        get() = controllers[1]

    private val rotationController
        get() = controllers[2]

    private fun updateControllerPosition() {

    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        canvas.drawRectangle(
                centerController.position.x,
                centerController.position.y,
                sizeController.position.x,
                sizeController.position.y,
                objectPaint,
                objectBorderPaint
        )

        drawControllers(canvas)
    }

    override fun initialize() {
        val centerX = width.toFloat() / 2.0f
        val centerY = height.toFloat() / 2.0f

        centerController.position.set(centerX - 300.0f, centerY - 200.0f)
        sizeController.position.set(centerX + 300.0f, centerY + 200.0f)

    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        centerController.position.set(matrix.mapPoint(centerController.position))
        sizeController.position.set(matrix.mapPoint(sizeController.position))
    }

    override fun generateShapeInfo(): Pair<ShapeData, Vector2Data> {
        val leftTop = centerController.position.viewToWorld()
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
