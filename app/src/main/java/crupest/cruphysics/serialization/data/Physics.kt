package crupest.cruphysics.serialization.data

import androidx.annotation.ColorInt
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//constants

const val SHAPE_TYPE_CIRCLE = "circle"
const val SHAPE_TYPE_RECTANGLE = "rectangle"

const val BODY_TYPE_STATIC = "static"
const val BODY_TYPE_DYNAMIC = "dynamic"



//data classes

@JsonClass(generateAdapter = true)
data class Vector2Data(
        val x: Double = 0.0,
        val y: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class CircleData(
        val center: Vector2Data = Vector2Data(),
        val radius: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class RectangleData(
        val center: Vector2Data = Vector2Data(),
        val width: Double = 0.0,
        val height: Double = 0.0
)


@JsonClass(generateAdapter = true)
data class ShapeData(
        val type: String = "",
        @field:Json(name = "circle_data") val circleData: CircleData? = null,
        @field:Json(name = "rectangle_data") val rectangleData: RectangleData? = null
)

@JsonClass(generateAdapter = true)
data class BodyAppearanceData(@ColorInt val color: Int = 0x000000)

@JsonClass(generateAdapter = true)
data class BodyData(
        val shape: ShapeData = ShapeData(),
        val type: String = BODY_TYPE_STATIC,
        val position: Vector2Data = Vector2Data(),
        val rotation: Double = 0.0,
        @field:Json(name = "linear_velocity") val linearVelocity: Vector2Data = Vector2Data(),
        @field:Json(name = "angular_velocity") val angularVelocity: Double = 0.0,
        val density: Double = 0.0,
        val friction: Double = 0.0,
        val restitution: Double = 0.0,
        val appearance: BodyAppearanceData = BodyAppearanceData()
)

@JsonClass(generateAdapter = true)
data class WorldData(
        val bodies: List<BodyData> = listOf(),
        val gravity: Vector2Data = Vector2Data()
)

private const val WORLD_VIEW_INIT_SCALE = 500.0

@JsonClass(generateAdapter = true)
data class CameraData(
        val translation: Vector2Data = Vector2Data(),
        val scale: Double = WORLD_VIEW_INIT_SCALE
)

//helper functions

fun CircleData.createShapeData() = ShapeData(type = SHAPE_TYPE_CIRCLE, circleData = this)
fun RectangleData.createShapeData() = ShapeData(type = SHAPE_TYPE_RECTANGLE, rectangleData = this)
