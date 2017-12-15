package crupest.cruphysics.physics.serialization

import org.dyn4j.geometry.*

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
        return Polygon(*vertices.map {
            @Suppress("UNCHECKED_CAST")
            unmapVector2(it as JsonObject)
        }.toTypedArray())
    }

    fun unmapShape(obj: JsonObject): Shape = when (obj.getStringProperty("type")) {
        "circle" -> unmapCircle(obj)
        "rectangle" -> unmapRectangle(obj)
        "polygon" -> unmapPolygon(obj)
        else -> throw UnmapException("Unknown shape type.")
    }
}
