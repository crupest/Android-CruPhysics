package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import crupest.cruphysics.R
import crupest.cruphysics.WorldManager
import crupest.cruphysics.utility.hitTestSquare
import crupest.cruphysics.physics.toVec2
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.setTimeout

/**
 * Created by crupest on 2017/11/26.
 * Class MainWorldCanvas.
 */
class MainWorldCanvas(context: Context, attributeSet: AttributeSet) : WorldCanvas(context, attributeSet) {

    private var singleLongTouchTimerTask: ScheduleTask? = null
    private var singleLongTouchDownPosition: PointF? = null

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

    @SuppressLint("InflateParams", "RtlHardcoded")
    private fun onSingleLongTouch(x: Float, y: Float) {
        post {
            val world = WorldManager.world

            val body = world.bodies.find {
                it.contains(viewToWorld(PointF(x, y)).toVec2())
            }

            if (body != null) {
                val layoutInflater = LayoutInflater.from(context)
                val rootView = layoutInflater.inflate(R.layout.object_popup_menu, null)
                val list = rootView.findViewById<ListView>(R.id.menu_list)

                val adapter = ArrayAdapter(
                        context,
                        R.layout.menu_item,
                        R.id.content,
                        context.resources.getStringArray(R.array.object_menu_item_list)
                )
                list.adapter = adapter

                val popupWindow = PopupWindow(
                        rootView,
                        500,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                )

                popupWindow.setBackgroundDrawable(ColorDrawable(
                        ContextCompat.getColor(context, R.color.menu_background)
                ))

                list.setOnItemClickListener { _, _, position, _ ->
                    when (position) {
                        0 -> {
                            world.removeBody(body)
                            invalidate()
                        }
                    }
                    popupWindow.dismiss()
                }

                popupWindow.showAsDropDown(
                        this,
                        x.toInt(),
                        y.toInt(),
                        Gravity.LEFT or Gravity.TOP
                )
            }
        }
    }
}
