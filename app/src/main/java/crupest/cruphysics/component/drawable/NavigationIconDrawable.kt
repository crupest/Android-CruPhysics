/*
 * Some code of this file is copied from
 * https://github.com/balysv/material-menu/blob/master/library/src/main/java/com/balysv/materialmenu/MaterialMenuDrawable.java
 * which is under Apache-2.0 license.
 */

package crupest.cruphysics.component.drawable


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.TypedValue

import android.graphics.Paint.Style
import crupest.cruphysics.utility.ScheduleTask
import crupest.cruphysics.utility.postOnMainThread
import crupest.cruphysics.utility.setInterval
import kotlin.math.roundToInt

class NavigationIconDrawable private constructor(private val lengthInfo: LengthInfo, initColor: Int) : Drawable() {

    companion object {
        private const val TIME_INTERVAL: Double = 1.0 / 60.0
        private const val ANIMATION_DURATION: Double = 0.5
        private const val STEP_PER_INTERVAL: Float = (1.0 / (ANIMATION_DURATION / TIME_INTERVAL)).toFloat()

        private const val BASE_DRAWABLE_WIDTH = 40.0f
        private const val BASE_DRAWABLE_HEIGHT = 40.0f
        private const val BASE_ICON_WIDTH = 20.0f

        private const val ARROW_MID_LINE_ANGLE = 180f
        private const val ARROW_TOP_LINE_ANGLE = 135f
        private const val ARROW_BOT_LINE_ANGLE = 225f

        private val transformRange: ClosedRange<Float> = IconState.BURGER.transformValue..IconState.ARROW.transformValue

        private fun Float.dpToPx(context: Context): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
        }
    }

    enum class IconState(val transformValue: Float) {
        BURGER(0.0f), ARROW(1.0f)
    }

    private data class LengthInfo(val pixelPerDip: Float, val scale: Float) {

        private fun Float.dip(): Float = this * pixelPerDip * scale

        val diph: Float = 0.5f.dip()
        val dip2: Float = 2.0f.dip()
        val dip3: Float = 3.0f.dip()
        val width: Float = BASE_DRAWABLE_WIDTH.dip()
        val height: Float = BASE_DRAWABLE_HEIGHT.dip()
        val iconWidth: Float = BASE_ICON_WIDTH.dip()
        val strokeWidth: Float = 2.0f.dip()
        val sidePadding: Float = (width - iconWidth) / 2.0f
        val topPadding: Float = (height - 5.0f * dip3) / 2.0f
    }

    private val iconPaint = Paint().apply {
        isAntiAlias = true
        style = Style.STROKE
        strokeWidth = lengthInfo.strokeWidth
        color = initColor
    }

    private var currentIconState: IconState = IconState.BURGER

    private var navigationIconState: NavigationIconState

    private val lock = Any()
    private var animationTask: ScheduleTask? = null

    init {
        setBounds(0, 0, lengthInfo.width.roundToInt(), lengthInfo.height.roundToInt())

        navigationIconState = NavigationIconState()
    }

    constructor(context: Context) : this(LengthInfo(1.0f.dpToPx(context), 1.0f), Color.WHITE)


    override fun draw(canvas: Canvas) {
        if (!isDrawableVisible) return

        val ratio = transformationValue

        if (rtlEnabled) {
            canvas.save()
            canvas.scale(-1f, 1f, 0f, 0f)
            canvas.translate(-lengthInfo.width, 0f)
        }

        drawTopLine(canvas, ratio)
        drawMiddleLine(canvas, ratio)
        drawBottomLine(canvas, ratio)

        if (rtlEnabled) {
            canvas.restore()
        }
    }

    private fun drawMiddleLine(canvas: Canvas, ratio: Float) {
        canvas.save()

        val rotation: Float = ratio * ARROW_MID_LINE_ANGLE // ARROW_TOP_LINE_ANGLE + (1 - ratio) * ARROW_BOT_LINE_ANGLE
        val pivotX = (lengthInfo.width / 2)
        val pivotY = (lengthInfo.width / 2)
        val startX = lengthInfo.sidePadding
        val startY = lengthInfo.topPadding + lengthInfo.dip3 / 2 * 5
        var stopX = lengthInfo.width - lengthInfo.sidePadding
        val stopY = lengthInfo.topPadding + lengthInfo.dip3 / 2 * 5

        stopX -= ratio * resolveStrokeModifier(ratio) / 2

        canvas.rotate(rotation, pivotX, pivotY)
        canvas.drawLine(startX, startY, stopX, stopY, iconPaint)

        canvas.restore()
    }

    private fun drawTopLine(canvas: Canvas, ratio: Float) {
        canvas.save()

        val rotation: Float = ratio * ARROW_BOT_LINE_ANGLE
        val pivotX: Float = (lengthInfo.width / 2)
        val pivotY: Float = (lengthInfo.height / 2)
        val rotation2 = 0f
        val pivotX2 = lengthInfo.width / 2 + lengthInfo.dip3 / 2
        val pivotY2 = lengthInfo.topPadding + lengthInfo.dip2

        var startX = lengthInfo.sidePadding
        val startY = lengthInfo.topPadding + lengthInfo.dip2
        var stopX = lengthInfo.width - lengthInfo.sidePadding
        val stopY = lengthInfo.topPadding + lengthInfo.dip2

        stopX -= resolveStrokeModifier(ratio)
        startX += lengthInfo.dip3 * ratio

        canvas.rotate(rotation, pivotX, pivotY)
        canvas.rotate(rotation2, pivotX2, pivotY2)
        canvas.drawLine(startX, startY, stopX, stopY, iconPaint)

        canvas.restore()
    }

    private fun drawBottomLine(canvas: Canvas, ratio: Float) {
        canvas.save()

        val rotation: Float = ARROW_TOP_LINE_ANGLE * ratio
        val pivotX: Float = lengthInfo.width / 2
        val pivotY: Float = lengthInfo.height / 2
        val rotation2 = 0f
        val pivotX2 = lengthInfo.width / 2 + lengthInfo.dip3 / 2
        val pivotY2 = lengthInfo.height - lengthInfo.topPadding - lengthInfo.dip2

        val startX: Float = lengthInfo.sidePadding + lengthInfo.dip3 * ratio
        val startY = lengthInfo.height - lengthInfo.topPadding - lengthInfo.dip2
        val stopX = lengthInfo.width - lengthInfo.sidePadding - resolveStrokeModifier(ratio)
        val stopY = lengthInfo.height - lengthInfo.topPadding - lengthInfo.dip2

        canvas.rotate(rotation, pivotX, pivotY)
        canvas.rotate(rotation2, pivotX2, pivotY2)
        canvas.drawLine(startX, startY, stopX, stopY, iconPaint)

        canvas.restore()
    }

    private fun resolveStrokeModifier(ratio: Float): Float =
            ratio * (lengthInfo.dip3 + lengthInfo.diph)

    override fun setAlpha(alpha: Int) {
        iconPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        iconPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    var color: Int
        get() = iconPaint.color
        set(value) {
            iconPaint.color = value
            invalidateSelf()
        }

    val iconState: IconState
        get() = currentIconState

    fun setState(state: IconState, animate: Boolean = true) {
        if (state == currentIconState && !isAnimating)
            return

        currentIconState = state

        if (animate)
            startAnimation()
        else {
            cancelAnimation()
            transformationValue = state.transformValue
        }
    }

    var isDrawableVisible: Boolean = true
        set(value) {
            field = value
            invalidateSelf()
        }

    var rtlEnabled: Boolean = false
        set(value) {
            field = value
            invalidateSelf()
        }

    var transformationValue: Float = 0.0f
        set(value) {
            field = value.coerceIn(transformRange)
            invalidateSelf()
        }

    val isAnimating: Boolean = synchronized(lock) {
        animationTask != null
    }

    private fun startAnimation() {
        synchronized(lock) {
            if (animationTask == null)
                animationTask = setInterval(TIME_INTERVAL) {
                    postOnMainThread {
                        val target = transformationValue + when (currentIconState) {
                            IconState.BURGER -> -STEP_PER_INTERVAL
                            IconState.ARROW -> STEP_PER_INTERVAL
                        }

                        transformationValue = target

                        if (target !in transformRange)
                            cancelAnimation()
                    }
                }
        }
    }

    private fun cancelAnimation() {
        synchronized(lock) {
            animationTask?.cancel()
            animationTask = null
        }
    }

    override fun getIntrinsicWidth(): Int {
        return lengthInfo.width.roundToInt()
    }

    override fun getIntrinsicHeight(): Int {
        return lengthInfo.height.roundToInt()
    }

    override fun getConstantState(): Drawable.ConstantState? {
        navigationIconState.changingConfigurations = changingConfigurations
        return navigationIconState
    }

    override fun mutate(): Drawable {
        navigationIconState = NavigationIconState()
        return this
    }

    private inner class NavigationIconState : Drawable.ConstantState() {
        private var changingConfigurations: Int = 0

        override fun newDrawable(): Drawable {
            val drawable = NavigationIconDrawable(lengthInfo, color)
            drawable.setState(currentIconState, false)
            drawable.isDrawableVisible = isDrawableVisible
            drawable.rtlEnabled = rtlEnabled
            return drawable
        }

        override fun getChangingConfigurations(): Int = changingConfigurations
        fun setChangingConfigurations(value: Int) {
            changingConfigurations = value
        }
    }
}
