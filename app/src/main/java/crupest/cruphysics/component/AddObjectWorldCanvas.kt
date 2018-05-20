package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.MotionEvent
import crupest.cruphysics.utility.fillPaint
import crupest.cruphysics.utility.hitTestSquare
import crupest.cruphysics.utility.strokePaint

/**
 * Created by crupest on 2017/11/4.
 * View component AddObjectWorldCanvas
 */
abstract class AddObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : WorldCanvas(context, attrs) {

    protected class ControllerDraggedEventArgs(val x: Float, val y: Float)
    protected class Controller(x: Float, y: Float,
                               val moveEventListener: (ControllerDraggedEventArgs) -> Unit) {
        constructor(draggedEventListener: (ControllerDraggedEventArgs) -> Unit)
                : this(0.0f, 0.0f, draggedEventListener)

        val position = PointF(x, y)
        val radius = 15.0f

        fun hitTest(x: Float, y: Float, strict: Boolean = true): Boolean =
                hitTestSquare(x, y, position.x, position.y, if (strict) radius else 40.0f)
    }

    protected abstract val controllers: Array<Controller>


    private var init = false


    protected val objectPaint: Paint = fillPaint(Color.BLUE)
    protected val objectBorderPaint: Paint = strokePaint(Color.BLACK, 3.0f)
    private val controllerPaint: Paint = fillPaint(Color.WHITE)
    private val controllerBorderPaint: Paint = strokePaint(Color.BLACK)


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
                draggedController.moveEventListener(ControllerDraggedEventArgs(event.x, event.y))
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

    protected abstract fun initialize()
    abstract fun generateShapeInfo(): ShapeInfo

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (!init) {
            initialize()
            init = true
        }
    }

    @get: ColorInt
    @setparam:ColorInt
    var color: Int
        get() = objectPaint.color
        set(value) {
            objectPaint.color = value
            repaint()
        }
}
