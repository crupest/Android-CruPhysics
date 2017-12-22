package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import crupest.cruphysics.utility.blackStrokePaint
import crupest.cruphysics.utility.drawArrow
import crupest.cruphysics.utility.hitTestSquare
import crupest.cruphysics.utility.whitePaint

/**
 * Created by crupest on 2017/12/22.
 * View component class [Vector2Picker].
 */
class Vector2Picker(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet) {

    init {
        setWillNotDraw(false)
    }

    private val controllerPosition = PointF(0.0f, 100.0f)
    private val controllerRadius = 15.0f
    private var controllerPressed = false

    private fun controllerHitTest(x: Float, y: Float): Boolean =
            hitTestSquare(x, y, controllerPosition.x, controllerPosition.y)

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
                    invalidate()
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
}
