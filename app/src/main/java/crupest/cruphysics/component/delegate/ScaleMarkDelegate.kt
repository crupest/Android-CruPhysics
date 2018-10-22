package crupest.cruphysics.component.delegate

import android.graphics.Canvas
import android.graphics.Color
import android.text.Html
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import crupest.cruphysics.utility.strokePaint
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class ScaleMarkDelegate(color: Int) {
    companion object {
        private const val expectScaleMarkWidth = 200
        private const val lineLabelGap = 5.0f
        private const val halfSideLength = 10.0f
        private const val labelTextSize = 40.0f
    }

    private val textPaint = TextPaint().also {
        it.color = color
        it.isAntiAlias = true
        it.textSize = labelTextSize
    }
    private lateinit var scaleMarkText: StaticLayout
    private var markLineLength: Float = 0.0f
    private val markLinePaint = strokePaint(color, 3.0f)


    fun draw(canvas: Canvas) {
        scaleMarkText.draw(canvas)
        canvas.translate(0.0f, scaleMarkText.height + halfSideLength + lineLabelGap)
        val lineLeft = (expectScaleMarkWidth - markLineLength) / 2.0f
        val lineRight = lineLeft + markLineLength
        canvas.drawLine(lineLeft, 0.0f, lineRight, 0.0f, markLinePaint)
        canvas.drawLine(lineLeft, -halfSideLength, lineLeft, halfSideLength, markLinePaint)
        canvas.drawLine(lineRight, -halfSideLength, lineRight, halfSideLength, markLinePaint)
    }

    fun recalculate(viewToWorld: (Float) -> Double, worldToView: (Double) -> Float) {
        val exponent = floor(log10(viewToWorld(expectScaleMarkWidth.toFloat()))).roundToInt()
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
                expectScaleMarkWidth,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ) else StaticLayout.Builder.obtain(textSource, 0, textSource.length, textPaint, expectScaleMarkWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0.0f, 1.0f)
                .setIncludePad(false).build()
    }

    val expectWidth = expectScaleMarkWidth
}
