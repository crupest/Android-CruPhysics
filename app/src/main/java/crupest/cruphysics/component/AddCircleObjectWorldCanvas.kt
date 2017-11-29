package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import crupest.cruphysics.distance
import crupest.cruphysics.mapPoint

/**
 * Created by crupest on 2017/11/6.
 * View component AddCircleObjectWorldCanvas
 */
class AddCircleObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    private val path = Path()
    private val objectPaint = Paint()
    private val objectBorderPaint = Paint()

    private var init = false

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
        path.addCircle(
                centerController.position.x,
                centerController.position.y,
                distance(centerController.position, radiusController.position),
                Path.Direction.CW
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawPath(path, objectPaint)
        canvas.drawPath(path, objectBorderPaint)

        drawControllers(canvas)
    }

    private fun reset() {
        val centerX = width.toFloat() / 2.0f
        val centerY = height.toFloat() / 2.0f

        centerController.position.set(centerX, centerY)
        radiusController.position.set(centerX + 300.0f, centerY)

        onControllerMove()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (!init) {
            reset()
            init = true
        }
    }

    val worldCenter: PointF
        get() = viewToWorld(centerController.position)

    val worldRadius: Float
        get() = distance(worldCenter, viewToWorld(radiusController.position))

    override fun onViewMatrixChanged(matrix: Matrix) {
        centerController.position.set(matrix.mapPoint(centerController.position))
        radiusController.position.set(matrix.mapPoint(radiusController.position))
        onControllerMove()
    }
}
