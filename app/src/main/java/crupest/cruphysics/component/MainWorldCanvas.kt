package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.hitTestSquare
import crupest.cruphysics.utility.setTimeout

/**
 * Created by crupest on 2017/11/26.
 * Class MainWorldCanvas.
 */
class MainWorldCanvas(context: Context?, attributeSet: AttributeSet?) : WorldCanvas(context, attributeSet) {

    private var singleLongTouchTimerTask: ScheduleTask? = null
    private var singleLongTouchDownPosition: PointF? = null

    lateinit var mainWorldDelegate: IMainWorldDelegate

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                // !!! Save the point because event object will be recycled.
                singleLongTouchDownPosition = PointF(event.x, event.y)

                singleLongTouchTimerTask = setTimeout(0.5) {
                    onSingleLongTouch(singleLongTouchDownPosition!!.x, singleLongTouchDownPosition!!.y)
                    singleLongTouchTimerTask = null
                    singleLongTouchDownPosition = null
                }
                super.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val position = singleLongTouchDownPosition
                if (position != null) {
                    if (!hitTestSquare(event.x, event.y, position.x, position.y, 40.0f)) {
                        singleLongTouchTimerTask?.cancel()
                        singleLongTouchTimerTask = null
                        singleLongTouchDownPosition = null
                    }
                }
                super.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_OUTSIDE,
            MotionEvent.ACTION_UP,
                // In fact, I think there is no need to check ACTION_POINTER_UP.
            MotionEvent.ACTION_POINTER_UP -> {
                singleLongTouchTimerTask?.cancel()
                singleLongTouchTimerTask = null
                singleLongTouchDownPosition = null
                super.onTouchEvent(event)
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun onSingleLongTouch(x: Float, y: Float) {
        post {
            val body = viewToWorld(x, y).let {
                mainWorldDelegate.bodyHitTest(it.x, it.y)
            }

            if (body != null) {
                CruPopupMenu(context, listOf(
                        "Delete" to { _ ->
                            mainWorldDelegate.removeBody(body)
                        }
                )).show(this, x.toInt(), y.toInt())
            }
        }
    }
}
