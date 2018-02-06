package crupest.cruphysics.physics

import android.graphics.Matrix
import org.dyn4j.dynamics.World

/**
 * Created by crupest on 2018/2/6.
 * Class [ViewWorld].
 */

class ViewWorld(val world: World, val viewMatrix: Matrix) {

    constructor(world: World): this(world, Matrix()) {
        resetView()
    }

    constructor(): this(World())

    fun resetView() {
        viewMatrix.reset()
        viewMatrix.postScale(100.0f, -100.0f)
    }
}
