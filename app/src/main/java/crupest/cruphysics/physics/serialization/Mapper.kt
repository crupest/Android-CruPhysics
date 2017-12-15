package crupest.cruphysics.physics.serialization

import crupest.cruphysics.physics.BodyUserData
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Polygon
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Vector2

/**
 * Created by crupest on 2017/12/15.
 * Class [Mapper].
 */
class Mapper {
    fun map(vector2: Vector2): JsonObject = mapOf(
            "x" to vector2.x,
            "y" to vector2.y
    )

    fun map(circle: Circle): JsonObject = mapOf(
            "type" to "circle",
            "center" to map(circle.center),
            "radius" to circle.radius
    )

    fun map(rectangle: Rectangle): JsonObject = mapOf(
            "type" to "rectangle",
            "center" to map(rectangle.center),
            "width" to rectangle.width,
            "height" to rectangle.height
    )

    fun map(polygon: Polygon): JsonObject = mapOf(
            "type" to "polygon",
            "vertices" to polygon.vertices.map {
                map(it)
            }
    )

    fun map(body: Body): JsonObject {
        val userData = body.userData
        if (userData is BodyUserData) {
            return userData.toJsonObject()
        } else {
            throw RuntimeException("User data of the body is not a BodyUserData.")
        }
    }
}
