package crupest.cruphysics.physics.view

import android.graphics.Paint


class BodyViewData(color: Int) {
    val paint = Paint().apply { this.color = color }

    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }
}
