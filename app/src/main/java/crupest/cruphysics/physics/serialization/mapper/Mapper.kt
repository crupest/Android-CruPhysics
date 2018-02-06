package crupest.cruphysics.physics.serialization.mapper

import android.graphics.Matrix
import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.physics.ViewWorld
import crupest.cruphysics.physics.serialization.JsonArray
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.utility.values
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.*

/**
 * Created by crupest on 2017/12/15.
 * Mapper functions.
 */

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

fun map(world: World): JsonObject =
        mapOf(
                "gravity" to crupest.cruphysics.physics.serialization.mapper.map(world.gravity),
                "bodies" to world.bodies.map {
                    crupest.cruphysics.physics.serialization.mapper.map(it)
                }
        )

fun map(matrix: Matrix): JsonArray = matrix.values

fun map(type: MassType): String = when (type) {
    MassType.NORMAL -> "dynamic"
    MassType.INFINITE -> "static"
    else -> throw UnsupportedOperationException("Other mass types are not supported.")
}

fun map(viewWorld: ViewWorld): JsonObject = mapOf(
        "version" to "1.0",
        "view_matrix" to map(viewWorld.viewMatrix),
        "world" to map(viewWorld.world)
)
