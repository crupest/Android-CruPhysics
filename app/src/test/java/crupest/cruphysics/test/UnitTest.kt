package crupest.cruphysics.test

import crupest.cruphysics.physics.fromData
import crupest.cruphysics.physics.toData
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UnitTest {
    @Test
    fun test() {

    }

    @Test
    fun cameraTest() {
        val cameraData = CameraData(Vector2Data(100.0, 100.0), 500.0)
        val convertBack = cameraData.fromData(100.0f, 100.0f).toData(100.0f, 100.0f)

        Assert.assertThat(convertBack, Matchers.equalTo(cameraData))
    }
}
