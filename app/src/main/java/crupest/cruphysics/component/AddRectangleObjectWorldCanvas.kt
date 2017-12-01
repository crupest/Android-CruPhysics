package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import crupest.cruphysics.MyRectF
import crupest.cruphysics.utility.mapPoint

/**
 * Created by crupest on 2017/11/17.
 * View component AddRectangleObjectWorldCanvas
 */
class AddRectangleObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    private val path = Path()
    private val objectPaint = Paint()
    private val objectBorderPaint = Paint()


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

    init {
        objectPaint.color = Color.BLUE
        objectBorderPaint.style = Paint.Style.STROKE
        objectBorderPaint.color = Color.BLACK
        objectBorderPaint.strokeWidth = 3.0f
    }

    private fun onControllerMove() {
        calculateShapeFromController()
        invalidate()
    }

    private fun calculateShapeFromController() {
        path.reset()
        path.addRect(
                positionController.position.x,
                positionController.position.y,
                sizeController.position.x,
                sizeController.position.y,
                Path.Direction.CW
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawPath(path, objectPaint)
        canvas.drawPath(path, objectBorderPaint)

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

    val worldRect: MyRectF
        get() {
            val lefttop = viewToWorld(positionController.position)
            val rightbottom = viewToWorld(sizeController.position)
            return MyRectF(lefttop.x, lefttop.y, rightbottom.x, rightbottom.y)
        }
}
