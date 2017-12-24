package crupest.cruphysics.utility

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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

fun Matrix.mapPoint(point: PointF): PointF = this.mapPoint(point.x, point.y)

fun Matrix.mapPath(path: Path): Path {
    val result = Path()
    path.transform(this, result)
    return result
}

val Matrix.values: List<Float>
    get() {
        val array = FloatArray(9)
        this.getValues(array)
        return array.asList()
    }

@SuppressLint("ParcelCreator")
@Parcelize
class ParcelableMatrix(val values: FloatArray = FloatArray(9)) : Parcelable {

    constructor(matrix: Matrix) : this(FloatArray(9)) {
        matrix.getValues(values)
    }

    fun toMatrix(): Matrix {
        val matrix = Matrix()
        matrix.setValues(values)
        return matrix
    }
}
