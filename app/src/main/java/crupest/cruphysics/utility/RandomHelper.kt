package crupest.cruphysics.utility

import android.graphics.Color
import kotlin.random.Random

object RandomHelper {
    fun generateRandomColor(): Int = Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
    )
}
