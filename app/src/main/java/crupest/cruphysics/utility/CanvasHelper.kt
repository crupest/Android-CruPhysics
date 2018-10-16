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
