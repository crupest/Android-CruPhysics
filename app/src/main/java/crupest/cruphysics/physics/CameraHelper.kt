package crupest.cruphysics.physics

import android.graphics.Matrix
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import crupest.cruphysics.utility.getValues

fun CameraData.translateCreate(x: Double, y: Double): CameraData {
    return this.copy(translation = Vector2Data(this.translation.x + x, this.translation.y + y))
}

fun CameraData.scaleCreate(scale: Double): CameraData {
    return this.copy(scale = this.scale * scale)
}


fun Matrix.toData(centerX: Float, centerY: Float): CameraData {
    // unalign center
    val copy = Matrix(this).also {
        it.postTranslate(-centerX, -centerY)
    }
    val values = copy.getValues()
    return CameraData(translation = Vector2Data(
            values[Matrix.MTRANS_X].toDouble(),
            values[Matrix.MTRANS_Y].toDouble()
    ), scale = values[Matrix.MSCALE_X].toDouble())
}

fun CameraData.fromData(matrix: Matrix, centerX: Float, centerY: Float) {
    matrix.reset()
    matrix.preTranslate(
            this.translation.x.toFloat(),
            this.translation.y.toFloat()
    )
    matrix.preScale(
            this.scale.toFloat(),
            -this.scale.toFloat()
    )
    //align center
    matrix.postTranslate(centerX, centerY)
}

fun CameraData.fromData(centerX: Float, centerY: Float): Matrix {
    val matrix = Matrix()
    this.fromData(matrix, centerX, centerY)
    return matrix
}
