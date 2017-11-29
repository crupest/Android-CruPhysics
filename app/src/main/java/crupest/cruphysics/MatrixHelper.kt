package crupest.cruphysics

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

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

fun Matrix.mapPoint(point: PointF): PointF {
    val array = floatArrayOf(point.x, point.y)
    this.mapPoints(array)
    return PointF(array[0], array[1])
}

fun Matrix.mapPath(path: Path): Path {
    val result = Path()
    path.transform(this, result)
    return result
}

class ParcelableMatrix() : Parcelable {

    private val values = FloatArray(9)

    constructor(matrix: Matrix) : this() {
        matrix.getValues(values)
    }

    constructor(parcel: Parcel) : this() {
        parcel.readFloatArray(values)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloatArray(values)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableMatrix> {
        override fun createFromParcel(parcel: Parcel): ParcelableMatrix {
            return ParcelableMatrix(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableMatrix?> {
            return arrayOfNulls(size)
        }
    }

    fun toMatrix(): Matrix {
        val matrix = Matrix()
        matrix.setValues(values)
        return matrix
    }
}
