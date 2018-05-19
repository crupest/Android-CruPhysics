package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import crupest.cruphysics.physics.createWorldViewMatrix
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.invertedMatrix
import crupest.cruphysics.utility.mapPoint
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by crupest on 2017/11/4.
 * View component [WorldCanvas]
 */

open class WorldCanvas(context: Context?, attributeSet: AttributeSet?)
    : SurfaceView(context, attributeSet) {

    lateinit var drawWorldDelegate: IDrawWorldDelegate

    val viewMatrix: Matrix = createWorldViewMatrix()

    private var created: AtomicBoolean = AtomicBoolean(false)

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                    created.set(true)
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                repaint()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                created.set(false)
            }
        })
    }

    fun PointF.worldToView(): PointF = worldToView(this.x, this.y)
    fun worldToView(x: Float, y: Float): PointF = viewMatrix.mapPoint(x, y)

    fun PointF.viewToWorld(): PointF = viewToWorld(this.x, this.y)
    fun viewToWorld(x: Float, y: Float): PointF = viewMatrix.invertedMatrix.mapPoint(x, y)

    fun getThumbnailViewMatrix(width: Int, height: Int, scale: Float) = Matrix(viewMatrix).also {
        it.postScale(scale, scale, this.width / 2.0f, this.height / 2.0f)
        //align center
        it.postTranslate((width - this.width) / 2.0f, (height - this.height) / 2.0f)
    }

    fun repaint() {
        if (!created.get())
            return

        val canvas = if (android.os.Build.VERSION.SDK_INT >= 26)
            holder.lockHardwareCanvas() else holder.lockCanvas()

        if (canvas != null)
            onPaint(canvas)

        holder.unlockCanvasAndPost(canvas)
    }

    protected open fun onPaint(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.save()
        canvas.concat(viewMatrix)
        drawWorldDelegate.draw(canvas)
        canvas.restore()
    }

    private val previousPointerPositionMap = mutableMapOf<Int, PointF>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        fun recordPointerPosition(index: Int) {
            previousPointerPositionMap.getOrPut(event!!.getPointerId(index)) {
                PointF()
            }.set(event.getX(index), event.getY(index))
        }

        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                recordPointerPosition(event.actionIndex)
                return true
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_OUTSIDE -> {
                when (event.pointerCount) {
                    1 -> {
                        val postMatrix = previousPointerPositionMap[event.getPointerId(0)]!!.run {
                            Matrix().apply {
                                postTranslate(
                                        event.getX(0) - this@run.x,
                                        event.getY(0) - this@run.y
                                )
                            }
                        }
                        viewMatrix.postConcat(postMatrix)
                        onViewMatrixChanged(postMatrix)
                        repaint()
                    }
                    2 -> {
                        val oldPosition1 = previousPointerPositionMap[event.getPointerId(0)]!!
                        val oldPosition2 = previousPointerPositionMap[event.getPointerId(1)]!!
                        val oldDistance = distance(oldPosition1, oldPosition2)
                        val newPosition1 = PointF(event.getX(0), event.getY(0))
                        val newPosition2 = PointF(event.getX(1), event.getY(1))
                        val newDistance = distance(newPosition1, newPosition2)
                        val scale = newDistance / oldDistance
                        val matrix = Matrix().apply {
                            postScale(scale, scale, width / 2.0f, height / 2.0f)
                        }
                        viewMatrix.postConcat(matrix)
                        onViewMatrixChanged(matrix)
                        repaint()
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

    /**
     *  @param matrix the matrix post-concat
     */
    protected open fun onViewMatrixChanged(matrix: Matrix) {

    }
}
