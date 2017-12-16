package crupest.cruphysics.physics.serialization

import crupest.cruphysics.physics.CircleBodyUserData
import crupest.cruphysics.physics.PolygonBodyUserData
import crupest.cruphysics.physics.RectangleBodyUserData
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.World
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
            unmapVector2(checkJsonObject(it) ?: throw UnmapException("$it is not a JsonObject."))
        }.toTypedArray())
    }

    fun unmapConvex(obj: JsonObject): Convex = when (obj.getStringProperty("type")) {
        "circle" -> unmapCircle(obj)
        "rectangle" -> unmapRectangle(obj)
        "polygon" -> unmapPolygon(obj)
        else -> throw UnmapException("Unknown shape type.")
    }

    fun unmapBody(obj: JsonObject): Body {
        val shape = unmapConvex(obj.getObjectProperty("shape"))

        val fixture = BodyFixture(shape)
        fixture.density = obj.getNumberProperty("density")
        fixture.friction = obj.getNumberProperty("friction")
        fixture.restitution = obj.getNumberProperty("restitution")

        val body = Body()
        body.translate(unmapVector2(obj.getObjectProperty("position")))
        body.rotate(obj.getNumberProperty("rotation"))
        body.addFixture(fixture)

        val color = obj.getNumberProperty("color").toInt()

        body.userData = when (shape) {
            is Circle -> CircleBodyUserData(body, color)
            is Rectangle -> RectangleBodyUserData(body, color)
            is Polygon -> PolygonBodyUserData(body, color)
            else -> throw IllegalStateException("Unreachable code!")
        }

        return body
    }

    fun unmapWorld(obj: JsonObject): World {
        val world = World()
        world.gravity = unmapVector2(obj.getObjectProperty("gravity"))
        obj.getArrayProperty("bodies").map {
            unmapBody(checkJsonObject(it) ?: throw UnmapException("$it is not a JsonObject."))
        }.forEach {
            world.addBody(it)
        }
        return world
    }
}
