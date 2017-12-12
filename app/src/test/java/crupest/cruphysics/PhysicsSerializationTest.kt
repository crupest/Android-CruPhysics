package crupest.cruphysics

import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.physics.CircleBodyUserData
import crupest.cruphysics.physics.serialization.moshi
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Circle
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Created by crupest on 2017/12/11.
 * Unit test for physics serialization.
 */
@RunWith(RobolectricTestRunner::class)
class PhysicsSerializationTest {
    @Test
    fun circleObjectSerializationTest() {
        //create body
        val body = Body()
        val circle = Circle(10.0)
        circle.translate(10.0, 10.0)
        val fixture = BodyFixture(circle)
        fixture.density = 1.0
        fixture.friction = 0.2
        fixture.restitution = 1.0
        body.addFixture(fixture)
        body.userData = CircleBodyUserData(body)

        //test
        val adapter = moshi.adapter(Map::class.java)
        print(adapter.toJson((body.userData as BodyUserData).toJsonObject()))
    }
}