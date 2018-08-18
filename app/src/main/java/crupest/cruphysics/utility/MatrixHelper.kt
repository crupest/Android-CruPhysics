package crupest.cruphysics.utility

import android.graphics.Matrix
import android.graphics.PointF

/**
 * Created by crupest on 2017/11/16.
 * Some Matrix helper functions.
 */

val Matrix.invertedMatrix: Matrix
    get() {
        val matrix = Matrix()
        this.invert(matrix)
        return matrix
    }

fun Matrix.mapPoint(x: Float, y: Float): PointF {
    val array = floatArrayOf(x, y)
    this.mapPoints(array)
    return PointF(array[0], array[1])
}

fun Matrix.getValues(): FloatArray {
    val array = FloatArray(9)
    this.getValues(array)
    return array
}
