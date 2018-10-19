package crupest.cruphysics

import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import crupest.cruphysics.physics.fromData
import crupest.cruphysics.physics.toData
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.Vector2Data
import org.hamcrest.Matchers.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AndroidUnitTest {
    @Test
    fun cameraTest() {
        val cameraData = CameraData(Vector2Data(100.0, 100.0), 500.0)
        val convertBack = cameraData.fromData(100.0f, 100.0f).toData(100.0f, 100.0f)

        Assert.assertThat(convertBack, equalTo(cameraData))
    }
}
