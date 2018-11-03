package crupest.cruphysics.component

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.setTimeout

/**
 * Created by crupest on 2017/11/26.
 * Class MainWorldCanvas.
 */
class MainWorldCanvas(context: Context?, attributeSet: AttributeSet?) : WorldCanvas(context, attributeSet) {

    private var singleLongTouchTimerTask: ScheduleTask? = null
    private var singleLongTouchDownPosition: PointF? = null

    var singleLongTouchListener: ((PointF) -> Unit)? = null

    override fun onTouchEventOverride(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                // !!! Save the point because event object will be recycled.
                singleLongTouchDownPosition = PointF(event.x, event.y)

                singleLongTouchTimerTask = setTimeout(0.5) {
                    post {
                        singleLongTouchDownPosition?.apply {
                            singleLongTouchListener?.invoke(PointF(x, y))
                            singleLongTouchTimerTask = null
                            singleLongTouchDownPosition = null
                        }
                    }
                }
                super.onTouchEventOverride(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val position = singleLongTouchDownPosition
                if (position != null) {
                    if (distance(event.x, event.y, position.x, position.y) > 40.0f) {
                        singleLongTouchTimerTask?.cancel()
                        singleLongTouchTimerTask = null
                        singleLongTouchDownPosition = null
                    }
                }
                super.onTouchEventOverride(event)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_OUTSIDE,
                // In fact, I think there is no need to check ACTION_POINTER_UP.
            MotionEvent.ACTION_POINTER_UP -> {
                singleLongTouchTimerTask?.cancel()
                singleLongTouchTimerTask = null
                singleLongTouchDownPosition = null
                super.onTouchEventOverride(event)
                return true
            }
        }

        return super.onTouchEventOverride(event)
    }
}
