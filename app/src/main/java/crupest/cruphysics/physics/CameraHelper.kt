package crupest.cruphysics.physics

import android.graphics.Matrix
import crupest.cruphysics.serialization.data.CameraData

fun CameraData.fromData(matrix: Matrix, centerX: Float, centerY: Float) {
    matrix.reset()

    //align center
    matrix.postTranslate(centerX, centerY)

    matrix.postTranslate(
            this.translation.x.toFloat(),
            this.translation.y.toFloat()
    )

    matrix.postScale(
            this.scale.toFloat(),
            -this.scale.toFloat(),
            centerX,
            centerY
    )
}

fun CameraData.fromData(centerX: Float, centerY: Float): Matrix {
    val matrix = Matrix()
    this.fromData(matrix, centerX, centerY)
    return matrix
}
