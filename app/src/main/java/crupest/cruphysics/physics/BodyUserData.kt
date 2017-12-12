package crupest.cruphysics.physics

import android.graphics.Canvas
import crupest.cruphysics.physics.serialization.JsonObject
import org.dyn4j.dynamics.Body


/**
 * Created by crupest on 2017/11/17.
 * Interface BodyUserData.
 */

interface BodyUserData {
    val body: Body
    fun draw(canvas: Canvas)
    fun toJsonObject(): JsonObject
}
