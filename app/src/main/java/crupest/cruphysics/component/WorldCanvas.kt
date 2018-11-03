package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withTranslation
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import crupest.cruphysics.R
import crupest.cruphysics.component.delegate.IDrawDelegate
import crupest.cruphysics.component.delegate.ScaleMarkDelegate
import crupest.cruphysics.physics.fromData
import crupest.cruphysics.physics.toData
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import crupest.cruphysics.utility.*
import crupest.cruphysics.viewmodel.MainViewModel
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created by crupest on 2017/11/4.
 * View component [WorldCanvas]
 */

open class WorldCanvas(context: Context?, attributeSet: AttributeSet?)
    : SurfaceView(context, attributeSet) {

    companion object {
        fun distance(p1: PointF, p2: PointF): Float =
                distance(p1.x, p1.y, p2.x, p2.y)
    }

    protected var mainViewModel: MainViewModel? = null
        private set

    private lateinit var drawWorldDelegate: IDrawDelegate
    private val scaleMarkDelegate = ScaleMarkDelegate(ContextCompat.getColor(context!!, R.color.icons))

    private val viewMatrix: Matrix = Matrix()

    private var surfaceCreated: AtomicBoolean = AtomicBoolean(false)
    private var init = false

    init {
        isFocusableInTouchMode = true

        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                surfaceCreated.set(true)
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                repaint()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                surfaceCreated.set(false)
            }
        })
    }

    // the robolectric doesn't implement it. So I have to implement it myself.
    // remember to delete it.
    private fun Matrix.myMapRadius(radius: Float): Float = this.getValues()[Matrix.MSCALE_X] * radius

    fun worldToView(radius: Double): Float = viewMatrix.myMapRadius(radius.toFloat())
    fun worldToView(x: Double, y: Double): PointF = viewMatrix.mapPoint(x.toFloat(), y.toFloat())
    fun viewToWorld(radius: Float): Double = viewMatrix.invertedMatrix.myMapRadius(radius).toDouble()
    fun viewToWorld(x: Float, y: Float): Vector2Data = viewMatrix.invertedMatrix.mapPoint(x, y).let {
        Vector2Data(it.x.toDouble(), it.y.toDouble())
    }

    private fun setCamera(camera: CameraData) {
        camera.fromData(viewMatrix, width.toFloat() / 2.0f, height.toFloat() / 2.0f)
        onCameraChangedCore(viewMatrix, false)
    }

    private fun generateCameraData(): CameraData = viewMatrix.toData(width.toFloat() / 2.0f, height.toFloat() / 2.0f)

    fun repaint() {
        if (!surfaceCreated.get())
            return

        //!!! Do not use hardware acceleration at current stage because some drawing methods are
        //!!! not supported especially when scaled.
        val canvas = holder.lockCanvas()

        if (canvas != null)
            onPaint(canvas)

        holder.unlockCanvasAndPost(canvas)
    }

    protected open fun onPaint(canvas: Canvas) {
        canvas.drawColor(context.getColorFromAttr(android.R.attr.windowBackground))
        canvas.withMatrix(viewMatrix) {
            drawWorldDelegate.draw(canvas)
        }
        canvas.withTranslation(width - scaleMarkDelegate.expectWidth - 50.0f, 50.0f) {
            scaleMarkDelegate.draw(canvas)
        }
    }

    private val previousPointerPositionMap = mutableMapOf<Int, PointF>()

    @SuppressLint("ClickableViewAccessibility")
    final override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN)
            requestFocus()

        if (onTouchEventOverride(event))
            return true

        return super.onTouchEvent(event)
    }

    open fun onTouchEventOverride(event: MotionEvent): Boolean {
        fun recordPointerPosition(index: Int) {
            previousPointerPositionMap.getOrPut(event.getPointerId(index)) {
                PointF()
            }.set(event.getX(index), event.getY(index))
        }

        when (event.actionMasked) {
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
                        onViewMatrixChangedCore(postMatrix)
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
                        onViewMatrixChangedCore(matrix)
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
        return false
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (gainFocus) {
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewMatrix.postTranslate(-oldw.toFloat() / 2.0f, -oldh.toFloat() / 2.0f)
        viewMatrix.postTranslate(w.toFloat() / 2.0f, h.toFloat() / 2.0f)

        if (!init) {
            onInitialize()
            init = true
        }

        onSizeChanged(w, h)
    }


    private fun onViewMatrixChangedCore(matrixPostConcat: Matrix) {
        onViewMatrixPostConcat(matrixPostConcat)
        onCameraChangedCore(viewMatrix)
    }

    /**
     *  Called when user operates and a view matrix is post concat.
     *  @param matrixPostConcat the matrix post-concat
     */
    protected open fun onViewMatrixPostConcat(matrixPostConcat: Matrix) {

    }

    private fun onCameraChangedCore(newMatrix: Matrix, updateViewModel: Boolean = true) {
        onCameraChanged(newMatrix)
        scaleMarkDelegate.recalculate(this::viewToWorld, this::worldToView)
        if (updateViewModel)
            mainViewModel?.updateLatestRecordCamera(generateCameraData())
    }

    protected open fun onCameraChanged(newMatrix: Matrix) {

    }

    protected open fun onInitialize() {

    }

    protected open fun onSizeChanged(width: Int, height: Int) {

    }

    fun bindViewModel(viewModel: MainViewModel, lifecycleOwner: LifecycleOwner) {
        check(mainViewModel == null) { "MainViewModel is already bound." }

        mainViewModel = viewModel

        setCamera(viewModel.camera.value!!)

        drawWorldDelegate = viewModel.drawWorldDelegate

        viewModel.camera.observe(lifecycleOwner, Observer {
            setCamera(it)
            repaint()
        })

        viewModel.registerWorldRepaintListener(lifecycleOwner) {
            repaint()
        }

        repaint()
    }
}
