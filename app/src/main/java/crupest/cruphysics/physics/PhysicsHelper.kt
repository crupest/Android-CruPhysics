package crupest.cruphysics.physics

import android.graphics.Matrix
import android.graphics.PointF
import org.dyn4j.geometry.Transform
import org.dyn4j.geometry.Vector2

/**
 * Created by crupest on 2017/11/17.
 * Helper functions for physics engine.
 */

fun PointF.toVec2(): Vector2 = Vector2(this.x.toDouble(), this.y.toDouble())

fun Transform.toMatrix(): Matrix {
    val matrix = Matrix()
    matrix.preTranslate(this.translationX.toFloat(), this.translationY.toFloat())
    matrix.preRotate(Math.toDegrees(this.rotation).toFloat())
    return matrix
}
