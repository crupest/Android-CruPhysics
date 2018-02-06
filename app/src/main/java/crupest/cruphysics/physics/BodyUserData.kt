package crupest.cruphysics.physics

import android.graphics.Canvas
import android.util.Log
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.physics.serialization.mapper.map
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

        Log.d("BodyUserData", "The body position is ${body.positionDebugString}")
        return mapOf(
                "type" to map(body.mass.type),
                "position" to map(body.transform.translation),
                "rotation" to body.transform.rotation,
                "color" to color,
                "density" to fixture.density,
                "friction" to fixture.friction,
                "restitution" to fixture.restitution,
                "linearVelocity" to map(body.linearVelocity),
                "angularVelocity" to body.angularVelocity
        )
    }
}
