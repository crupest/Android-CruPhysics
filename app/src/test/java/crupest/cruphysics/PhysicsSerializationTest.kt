package crupest.cruphysics

import com.squareup.moshi.JsonAdapter
import crupest.cruphysics.physics.serialization.JsonObject
import crupest.cruphysics.physics.serialization.Mapper
import crupest.cruphysics.physics.serialization.Unmapper
import crupest.cruphysics.physics.serialization.moshi
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Polygon
import org.dyn4j.geometry.Vector2
import org.junit.Assert
import org.junit.Test

/**
 * Created by crupest on 2017/12/11.
 * Unit test for physics serialization.
 */
class PhysicsSerializationTest {

    private val mapper = Mapper()
    private val unmapper = Unmapper()
    private val adapter: JsonAdapter<JsonObject> = moshi.adapter<JsonObject>(Map::class.java)

    @Test
    fun circleSerializationTest() {
        val circle = Circle(20.0)
        circle.translate(10.0, 5.98)

        val jsonString = adapter.toJson(mapper.map(circle))
        val circle2 = unmapper.unmapCircle(adapter.fromJson(jsonString) as JsonObject)

        Assert.assertEquals(circle.radius, circle2.radius, 0.0)
        Assert.assertEquals(circle.center.x, circle2.center.x, 0.0)
        Assert.assertEquals(circle.center.y, circle2.center.y, 0.0)
    }

    @Test
    fun polygonSerializationTest() {
        val polygon = Polygon(
                Vector2(1.0, 0.0),
                Vector2(0.0, 1.0),
                Vector2(0.0, -1.0)
        )

        val jsonString = adapter.toJson(mapper.map(polygon))
        val polygon2 = unmapper.unmapPolygon(adapter.fromJson(jsonString) as JsonObject)


        Assert.assertEquals(polygon.vertices[0], polygon2.vertices[0])
        Assert.assertEquals(polygon.vertices[1], polygon2.vertices[1])
        Assert.assertEquals(polygon.vertices[2], polygon2.vertices[2])
    }
}
