package crupest.cruphysics.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.text.Html
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import crupest.cruphysics.utility.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Created by crupest on 2017/11/4.
 * View component [WorldCanvas]
 */

open class WorldCanvas(context: Context?, attributeSet: AttributeSet?)
    : SurfaceView(context, attributeSet) {

    companion object {
        private const val scaleMarkWidth = 200


        //Region: camera

        private const val WORLD_VIEW_INIT_SCALE = 500.0f

        private fun Matrix.resetAsCamera(centerX: Float, centerY: Float) {
            this.reset()
            this.postTranslate(centerX, centerY)
            this.preScale(WORLD_VIEW_INIT_SCALE, -WORLD_VIEW_INIT_SCALE)
        }

        private fun Matrix.toData(centerX: Float, centerY: Float): CameraData {
            val copy = Matrix(this).also {
                it.postTranslate(-centerX, -centerY)
            }
            val values = copy.getValues()
            return CameraData(translation = Vector2Data(
                    values[Matrix.MTRANS_X].toDouble(),
                    values[Matrix.MTRANS_Y].toDouble()
            ), scale = values[Matrix.MSCALE_X].toDouble())
        }

        private fun CameraData.fromData(matrix: Matrix, centerX: Float, centerY: Float) {
            matrix.reset()
            matrix.preTranslate(
                    this.translation.x.toFloat(),
                    this.translation.y.toFloat()
            )
            matrix.preScale(
                    this.scale.toFloat(),
                    -this.scale.toFloat()
            )
            matrix.postTranslate(centerX, centerY)
        }
    }

    lateinit var drawWorldDelegate: IDrawWorldDelegate

    private val viewMatrix: Matrix = Matrix()

    private val textPaint = TextPaint().also {
        it.color = Color.BLACK
        it.isAntiAlias = true
        it.textSize = 40.0f
    }
    private lateinit var scaleMarkText: StaticLayout
    private var markLineLength: Float = 0.0f
    private val markLinePaint = strokePaint(Color.BLACK, 3.0f)

    private var created: AtomicBoolean = AtomicBoolean(false)

    private var init = false

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

    fun worldToView(radius: Double): Float = viewMatrix.mapRadius(radius.toFloat())
    fun worldToView(x: Double, y: Double): PointF = viewMatrix.mapPoint(x.toFloat(), y.toFloat())
    fun viewToWorld(radius: Float): Double = viewMatrix.invertedMatrix.mapRadius(radius).toDouble()
    fun viewToWorld(x: Float, y: Float): Vector2Data = viewMatrix.invertedMatrix.mapPoint(x, y).let {
        Vector2Data(it.x.toDouble(), it.y.toDouble())
    }

    fun setCamera(camera: CameraData) {
        camera.fromData(viewMatrix, width.toFloat() / 2.0f, height.toFloat() / 2.0f)
        recalculateScaleMark()
        onSetCamera()
    }

    fun resetCamera() {
        viewMatrix.resetAsCamera(width.toFloat() / 2.0f, height.toFloat() / 2.0f)
        recalculateScaleMark()
        onSetCamera()
    }

    fun generateCameraData(): CameraData = viewMatrix.toData(width.toFloat() / 2.0f, height.toFloat() / 2.0f)

    fun getThumbnailViewMatrix(width: Int, height: Int, scale: Float) = Matrix(viewMatrix).also {
        it.postScale(scale, scale, this.width / 2.0f, this.height / 2.0f)
        //align center
        it.postTranslate((width - this.width) / 2.0f, (height - this.height) / 2.0f)
    }

    fun repaint() {
        if (!created.get())
            return

        //!!! Do not use hardware acceleration at current stage because some drawing methods are
        //!!! not supported especially when scaled.
        val canvas = holder.lockCanvas()

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
        drawScaleMark(canvas)
    }

    private fun drawScaleMark(canvas: Canvas) {
        val markLeft = width - scaleMarkWidth - 50.0f
        val markTop = 30.0f
        val halfSideLength = 10.0f

        canvas.save()
        canvas.translate(markLeft, markTop)
        scaleMarkText.draw(canvas)
        canvas.translate(0.0f, scaleMarkText.height + halfSideLength + 5.0f)
        val lineLeft = (scaleMarkWidth - markLineLength) / 2.0f
        val lineRight = lineLeft + markLineLength
        canvas.drawLine(lineLeft, 0.0f, lineRight, 0.0f, markLinePaint)
        canvas.drawLine(lineLeft, -halfSideLength, lineLeft, halfSideLength, markLinePaint)
        canvas.drawLine(lineRight, -halfSideLength, lineRight, halfSideLength, markLinePaint)
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
                        onViewMatrixChangedInternal(postMatrix)
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
                        onViewMatrixChangedInternal(matrix)
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (!init) {
            onInitialize()
            init = true
        }

        viewMatrix.postTranslate(-oldw.toFloat() / 2.0f, -oldh.toFloat() / 2.0f)
        viewMatrix.postTranslate(w.toFloat() / 2.0f, h.toFloat() / 2.0f)

        onSizeChanged(w, h)
    }

    private fun recalculateScaleMark() {
        val exponent = floor(log10(viewToWorld(scaleMarkWidth.toFloat()))).roundToInt()
        markLineLength = worldToView(10.0.pow(exponent))

        val html = "10<sup><small>$exponent</small></sup>m"

        @Suppress("DEPRECATION")
        val textSource = if (android.os.Build.VERSION.SDK_INT >= 24)
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        else
            Html.fromHtml(html)

        @Suppress("DEPRECATION")
        scaleMarkText = if (android.os.Build.VERSION.SDK_INT < 23) StaticLayout(textSource,
                textPaint,
                scaleMarkWidth,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ) else StaticLayout.Builder.obtain(textSource, 0, textSource.length, textPaint, scaleMarkWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0.0f, 1.0f)
                .setIncludePad(false).build()
    }

    private fun onViewMatrixChangedInternal(matrix: Matrix) {
        recalculateScaleMark()
        onViewMatrixChanged(matrix)
    }

    /**
     *  @param matrix the matrix post-concat
     */
    protected open fun onViewMatrixChanged(matrix: Matrix) {

    }

    protected open fun onSetCamera() {

    }

    protected open fun onInitialize() {

    }

    protected open fun onSizeChanged(width: Int, height: Int) {

    }
}
