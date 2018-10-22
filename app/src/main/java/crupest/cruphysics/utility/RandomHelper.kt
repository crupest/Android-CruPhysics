package crupest.cruphysics.utility

import android.graphics.Color
import java.util.Random

object RandomHelper {
    private val random = Random()

    fun generateRandomColor(): Int = Color.rgb(
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
    )
}
