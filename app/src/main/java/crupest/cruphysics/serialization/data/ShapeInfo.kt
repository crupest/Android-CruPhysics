package crupest.cruphysics.serialization.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShapeInfo(
        val shapeData: ShapeData,
        val position: Vector2Data,
        val rotation: Double
)
