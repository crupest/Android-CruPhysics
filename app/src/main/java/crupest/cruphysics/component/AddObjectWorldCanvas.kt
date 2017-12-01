package crupest.cruphysics.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by crupest on 2017/11/4.
 * View component AddObjectWorldCanvas
 */
abstract class AddObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : WorldCanvas(context, attrs) {

    protected class ControllerMoveEventArgs(val controller: Controller, val oldPosition: PointF, val newPosition: PointF)
    protected class Controller(x: Float, y: Float, val moveEventListener: (ControllerMoveEventArgs) -> Unit) {
        constructor(moveEventListener: (ControllerMoveEventArgs) -> Unit) : this(0.0f, 0.0f, moveEventListener)

        val position = PointF(x, y)
        val radius = 15.0f

        fun hitTest(x: Float, y: Float, strict: Boolean = true): Boolean {
            val f = { radius: Float ->
                x > position.x - radius &&
                        x < position.x + radius &&
                        y > position.y - radius &&
                        y < position.y + radius
            }

            return if (strict) f(radius) else f(40.0f)
        }
    }

    protected abstract val controllers: Array<Controller>
    private var init = false

    private val controllerPaint = Paint()
    private val controllerBorderPaint = Paint()

    private var draggedControllerIndex = -1
    private val draggedController: Controller
        get() = controllers[draggedControllerIndex]

    private fun decideWhichController(x: Float, y: Float): Int {
        val strictResult = controllers.indexOfFirst { it.hitTest(x, y) }
        return if (strictResult == -1)
            controllers.indexOfFirst { it.hitTest(x, y, false) }
        else
            strictResult
    }

    init {
        controllerPaint.color = Color.WHITE
        controllerBorderPaint.style = Paint.Style.STROKE
        controllerBorderPaint.color = Color.BLACK
    }

    //Remember to call this to draw controllers.
    protected fun drawControllers(canvas: Canvas) {
        for (controller in controllers) {
            canvas.drawCircle(controller.position.x, controller.position.y, controller.radius, controllerPaint)
            canvas.drawCircle(controller.position.x, controller.position.y, controller.radius, controllerBorderPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            val whichController = decideWhichController(event.x, event.y)
            if (whichController != -1) {
                draggedControllerIndex = whichController
                return true
            }
        } else if (event.action == MotionEvent.ACTION_MOVE
                || event.action == MotionEvent.ACTION_OUTSIDE) {
            if (draggedControllerIndex != -1) {
                val oldPosition = PointF(draggedController.position.x, draggedController.position.y)
                val newPosition = PointF(event.x, event.y)
                draggedController.position.set(newPosition)
                draggedController.moveEventListener(ControllerMoveEventArgs(
                        draggedController, oldPosition, newPosition
                ))
                return true
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (draggedControllerIndex != -1) {
                draggedControllerIndex = -1
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    protected abstract fun reset()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (!init) {
            reset()
            init = true
        }
    }
}
