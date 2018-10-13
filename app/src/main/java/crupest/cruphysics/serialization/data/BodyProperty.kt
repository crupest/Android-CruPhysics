package crupest.cruphysics.serialization.data

import androidx.annotation.ColorInt
import com.squareup.moshi.JsonClass
import crupest.cruphysics.utility.generateRandomColor

@JsonClass(generateAdapter = true)
data class BodyProperty(
        var type: String = BODY_TYPE_STATIC,
        var density: Double = 1.0,
        var restitution: Double = 0.0,
        var friction: Double = 0.2,
        @ColorInt var color: Int = generateRandomColor()
)
