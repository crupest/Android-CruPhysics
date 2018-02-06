package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.*
import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.physics.ViewWorld
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.invertedMatrix
import crupest.cruphysics.utility.mapPoint

/**
 * Created by crupest on 2017/11/4.
 * View component WorldCanvas
 */

open class WorldCanvas(context: Context?, attributeSet: AttributeSet?)
    : SurfaceView(context, attributeSet) {

    lateinit var viewWorld: ViewWorld

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                this@WorldCanvas.setWillNotDraw(false)
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }
        })
    }

    fun worldToView(point: PointF): PointF = viewWorld.viewMatrix.mapPoint(point)

    fun viewToWorld(point: PointF): PointF = viewWorld.viewMatrix.invertedMatrix.mapPoint(point)

    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawColor(Color.WHITE)
        canvas.save()
        canvas.concat(viewWorld.viewMatrix)

        for (body in viewWorld.world.bodies) {
            (body.userData as BodyUserData).draw(canvas)
        }

        canvas.restore()
    }

    private val previousPointerPositionMap = mutableMapOf<Int, PointF>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        fun recordPointerPosition(index: Int) {
            val position = previousPointerPositionMap.getOrPut(event!!.getPointerId(index)) {
                PointF()
            }
            position.set(event.getX(index), event.getY(index))
        }

        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                recordPointerPosition(event.actionIndex)
                return true
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_OUTSIDE -> {
                when (event.pointerCount) {
                    1 -> {
                        val oldPosition = previousPointerPositionMap[event.getPointerId(0)]!!
                        val matrix = Matrix()
                        matrix.postTranslate(
                                event.getX(0) - oldPosition.x,
                                event.getY(0) - oldPosition.y
                        )
                        viewWorld.viewMatrix.postConcat(matrix)
                        onViewMatrixChanged(matrix)
                        invalidate()
                    }
                    2 -> {
                        val oldPosition1 = previousPointerPositionMap[event.getPointerId(0)]!!
                        val oldPosition2 = previousPointerPositionMap[event.getPointerId(1)]!!
                        val oldDistance = distance(oldPosition1, oldPosition2)
                        val newPosition1 = PointF(event.getX(0), event.getY(0))
                        val newPosition2 = PointF(event.getX(1), event.getY(1))
                        val newDistance = distance(newPosition1, newPosition2)
                        val scale = newDistance / oldDistance
                        val matrix = Matrix()
                        matrix.postScale(scale, scale, width / 2.0f, height / 2.0f)
                        viewWorld.viewMatrix.postConcat(matrix)
                        onViewMatrixChanged(matrix)
                        invalidate()
                    }
                }
                for (index in 0 until event.pointerCount)
                    recordPointerPosition(index)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                previousPointerPositionMap.remove(event.getPointerId(event.actionIndex))
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // matrix: the matrix post-concat
    protected open fun onViewMatrixChanged(matrix: Matrix) {

    }
}
