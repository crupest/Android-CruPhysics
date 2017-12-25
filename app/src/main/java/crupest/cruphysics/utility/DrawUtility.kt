package crupest.cruphysics.utility

import android.graphics.*
import kotlin.math.atan2

/**
 * Created by crupest on 2017/12/22.
 * Utility functions for drawing.
 */

object PathPool {
    private val list = mutableListOf<Path>()

    fun obtain(): Path {
        if (list.isEmpty()) {
            return Path()
        }
        val path = list.last()
        list.remove(path)
        path.reset()
        return path
    }

    fun recycle(path: Path) {
        list.add(path)
    }
}

val blackPaint by lazy {
    val paint = Paint()
    paint.color = Color.BLACK
    paint.style = Paint.Style.FILL
    return@lazy paint
}

val whitePaint by lazy {
    val paint = Paint()
    paint.color = Color.WHITE
    paint.style = Paint.Style.FILL
    return@lazy paint
}

val blackStrokePaint by lazy {
    val paint = Paint()
    paint.color = Color.BLACK
    paint.strokeWidth = 1.0f
    paint.style = Paint.Style.STROKE
    return@lazy paint
}

fun Path.moveTo(point: PointF) {
    this.moveTo(point.x, point.y)
}

fun Path.lineTo(point: PointF) {
    this.lineTo(point.x, point.y)
}

fun Canvas.drawArrow(
        start: PointF = PointF(),
        end: PointF = PointF(),
        arrowHeadWidth: Float = 30.0f,
        stroke: Paint = blackStrokePaint,
        fill: Paint = blackPaint
) {

    val deltaX = end.x - start.x
    val deltaY = end.y - start.y

    if (deltaX == 0.0f || deltaY == 0.0f)
        return

    val path = PathPool.obtain()

    val matrix = Matrix()
    matrix.postRotate(atan2(deltaY, deltaX))
    matrix.postTranslate(-arrowHeadWidth, arrowHeadWidth)
    val arrowHead1 = matrix.mapPoint(end)
    matrix.postTranslate(0.0f, -arrowHeadWidth * 2.0f)
    val arrowHead2 = matrix.mapPoint(end)

    path.moveTo(start)
    path.lineTo(end)
    path.lineTo(arrowHead1)
    path.lineTo(arrowHead2)
    path.lineTo(end)

    this.drawPath(path, fill)
    this.drawPath(path, stroke)
    PathPool.recycle(path)
}
