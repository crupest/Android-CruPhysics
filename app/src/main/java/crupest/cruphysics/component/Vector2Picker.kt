package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import crupest.cruphysics.Event
import crupest.cruphysics.utility.*
import org.dyn4j.geometry.Vector2
import kotlin.math.log
import kotlin.math.pow

/**
 * Created by crupest on 2017/12/22.
 * View component class [Vector2Picker].
 */
class Vector2Picker(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet) {

    init {
        setWillNotDraw(false)
    }

    class VectorChangeEventArgs(val newVector: Vector2)

    val vectorChangeEvent = Event<VectorChangeEventArgs>()

    private val controllerPosition = PointF()
    private val controllerRadius = 15.0f
    private var controllerPressed = false

    private val myVector = Vector2(0.0, -10.0)

    private val myMatrix = Matrix()

    private fun controllerHitTest(x: Float, y: Float): Boolean =
            hitTestSquare(x, y, controllerPosition.x, controllerPosition.y)

    //the base number of the gradient
    private var gradientBase: Double = 1.1

    //refresh vector from position of controller
    private fun controllerToVector() {
        val point = myMatrix.invertedMatrix.mapPoint(controllerPosition)
        myVector.x = gradientBase.pow(point.x.toDouble())
        myVector.y = gradientBase.pow(point.y.toDouble())
        vectorChangeEvent.raise(VectorChangeEventArgs(myVector))
    }

    //refresh position of controller from vector
    private fun vectorToController() {
        controllerPosition.set(
                myMatrix.mapPoint(
                        log(myVector.x, gradientBase).toFloat(),
                        log(myVector.y, gradientBase).toFloat()
                )
        )
    }

    private fun onControllerMove() {
        invalidate()
        controllerToVector()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (controllerHitTest(event.x, event.y)) {
                    controllerPressed = true
                    return true
                }
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_OUTSIDE -> {
                if (controllerPressed) {
                    controllerPosition.set(event.x, event.y)
                    onControllerMove()
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                controllerPressed = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawArrow(end = controllerPosition)

        canvas.drawCircle(controllerPosition.x, controllerPosition.y, controllerRadius, whitePaint)
        canvas.drawCircle(controllerPosition.x, controllerPosition.y, controllerRadius, blackStrokePaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        myMatrix.reset()
        myMatrix.postTranslate(w.toFloat() / 2.0f, h.toFloat() / 2.0f)
        myMatrix.postScale(1.0f, -1.0f)

        vectorToController()
        invalidate()
    }

    // Don't set the x/y via getter.
    var vector: Vector2
        get() = myVector
        set(value) {
            myVector.set(value)
            vectorToController()
            invalidate()
            vectorChangeEvent.raise(VectorChangeEventArgs(myVector))
        }
}
