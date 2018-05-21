package crupest.cruphysics.utility

import android.graphics.Canvas
import android.graphics.Paint


fun Canvas.drawCircle(x: Float, y: Float, radius: Float, fill: Paint, stroke: Paint) {
    this.drawCircle(x, y, radius, fill)
    this.drawCircle(x, y, radius, stroke)
}

fun Canvas.drawRectangle(left: Float, top: Float, right: Float, bottom: Float,
                         fill: Paint, stroke: Paint) {
    this.drawRect(left, top, right, bottom, fill)
    this.drawRect(left, top, right, bottom, stroke)
}

fun fillPaint(color: Int) = Paint().apply {
    this.color = color
}

fun strokePaint(color: Int) = Paint().apply {
    this.style = Paint.Style.STROKE
    this.color = color
}

fun strokePaint(color: Int, width: Float) = Paint().apply {
    this.style = Paint.Style.STROKE
    this.color = color
    this.strokeWidth = width
}

/*
https://stackoverflow.com/questions/19908003/getting-height-of-text-view-before-rendering-to-layout

fun measureText(
        text: CharSequence,
        textSize: Int, // in pixels
        deviceWidth: Int, // in pixels
        padding: Int // in pixels
): Int {

    val myTextPaint = TextPaint()
    myTextPaint.isAntiAlias = true
    // this is how you would convert sp to pixels based on screen density
    //myTextPaint.setTextSize(16 * context.getResources().getDisplayMetrics().density);
    myTextPaint.textSize = textSize.toFloat()
    val alignment = Layout.Alignment.ALIGN_NORMAL
    val spacingMultiplier = 1f
    val spacingAddition = padding.toFloat() // optionally apply padding here
    val includePadding = padding != 0
    val myStaticLayout = StaticLayout(text, myTextPaint, deviceWidth, alignment, spacingMultiplier, spacingAddition, includePadding)
    return myStaticLayout.height
}
*/
