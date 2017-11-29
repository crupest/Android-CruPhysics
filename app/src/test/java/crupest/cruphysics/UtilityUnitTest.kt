package crupest.cruphysics

import crupest.cruphysics.utility.setInterval
import crupest.cruphysics.utility.setTimeout
import org.junit.Assert
import org.junit.Test

class UtilityUnitTest {
    @Test
    fun timerFunctionsTest() {
        var i = 0
        var result = false
        val task = setInterval(1.0) {
            i++
        }

        setTimeout(4.5) {
            result = i == 5
            task.cancel()
        }

        Thread.sleep(5000)
        Assert.assertTrue(result)
    }
}
