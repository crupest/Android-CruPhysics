package crupest.cruphysics.physics.serialization

import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Polygon
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Vector2

/**
 * Created by crupest on 2017/12/15.
 * Class [Unmapper].
 */

class Unmapper {
    fun unmapVector2(obj: JsonObject): Vector2 = Vector2(
            obj.getNumberProperty("x"),
            obj.getNumberProperty("y")
    )

    fun unmapCircle(obj: JsonObject): Circle {
        if (obj.getStringProperty("type") != "circle") {
            throw UnmapException("Shape type is not circle.")
        }
        val circle = Circle(obj.getNumberProperty("radius"))
        circle.center.set(unmapVector2(obj.getObjectProperty("center")))
        return circle
    }

    fun unmapRectangle(obj: JsonObject): Rectangle {
        if (obj.getStringProperty("type") != "rectangle") {
            throw UnmapException("Shape type is not rectangle.")
        }
        val rectangle = Rectangle(
                obj.getNumberProperty("width"),
                obj.getNumberProperty("height")
        )
        rectangle.center.set(unmapVector2(obj.getObjectProperty("center")))
        return rectangle
    }

    fun unmapPolygon(obj: JsonObject): Polygon {
        if (obj.getStringProperty("type") != "polygon") {
            throw UnmapException("Shape type is not polygon.")
        }
        val vertices = obj.getArrayProperty("vertices")
        return Polygon(*Array(vertices.size) {
            @Suppress("UNCHECKED_CAST")
            unmapVector2(vertices[it] as JsonObject)
        })
    }
}
