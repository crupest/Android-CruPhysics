package crupest.cruphysics.physics.serialization

import org.dyn4j.geometry.Circle

/**
 * Created by crupest on 2017/12/15.
 * Class [Unmapper].
 */

class Unmapper {
    fun unmapCircle(obj: JsonObject): Circle {
        if (obj.getStringProperty("type") != "circle") {
            throw UnmapException("Shape type is not circle.")
        }
        val circle = Circle(obj.getNumberProperty("radius"))
        val center = obj.getObjectProperty("center")
        circle.translate(center.getNumberProperty("x"), center.getNumberProperty("y"))
        return circle
    }
}
