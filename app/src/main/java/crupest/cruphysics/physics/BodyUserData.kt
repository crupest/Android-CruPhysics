package crupest.cruphysics.physics

import android.graphics.Canvas
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.physics.serialization.toJsonObject
import org.dyn4j.dynamics.Body


/**
 * Created by crupest on 2017/11/17.
 * Class [BodyUserData].
 */

abstract class BodyUserData(val body: Body) {
    abstract fun draw(canvas: Canvas)
    abstract fun toJsonObject(): JsonObject
    abstract var color: Int

    protected fun basePropertyToJsonObject(): JsonObject {
        val fixture = body.fixtures[0]
        return mapOf(
                "position" to body.transform.translation.toJsonObject(),
                "rotation" to body.transform.rotation,
                "color" to color,
                "density" to fixture.density,
                "friction" to fixture.friction,
                "restitution" to fixture.restitution
        )
    }
}
