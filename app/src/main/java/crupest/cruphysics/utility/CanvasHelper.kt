package crupest.cruphysics.utility

import android.graphics.Canvas
import android.graphics.Paint

fun Canvas.drawCircle(x: Float, y: Float, radius: Float, vararg paints: Paint) {
    paints.forEach { this.drawCircle(x, y, radius, it) }
}

fun Canvas.drawRectangle(left: Float, top: Float, right: Float, bottom: Float, vararg paints: Paint) {
    paints.forEach { this.drawRect(left, top, right, bottom, it) }
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
