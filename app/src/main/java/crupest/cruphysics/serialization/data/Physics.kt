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
        var x: Double = 0.0,
        var y: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class CircleData(
        var center: Vector2Data = Vector2Data(),
        var radius: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class RectangleData(
        var center: Vector2Data = Vector2Data(),
        var width: Double = 0.0,
        var height: Double = 0.0
)


@JsonClass(generateAdapter = true)
data class ShapeData(
        var type: String = "",
        @field:Json(name = "circle_data") var circleData: CircleData? = null,
        @field:Json(name = "rectangle_data") var rectangleData: RectangleData? = null
)

@JsonClass(generateAdapter = true)
data class BodyAppearanceData(@ColorInt var color: Int = 0x000000)

@JsonClass(generateAdapter = true)
data class BodyData(
        var shape: ShapeData = ShapeData(),
        var type: String = BODY_TYPE_STATIC,
        var position: Vector2Data = Vector2Data(),
        var rotation: Double = 0.0,
        @field:Json(name = "linear_velocity") var linearVelocity: Vector2Data = Vector2Data(),
        @field:Json(name = "angular_velocity") var angularVelocity: Double = 0.0,
        var density: Double = 0.0,
        var friction: Double = 0.0,
        var restitution: Double = 0.0,
        var appearance: BodyAppearanceData = BodyAppearanceData()
)

@JsonClass(generateAdapter = true)
data class WorldData(
        var bodies: List<BodyData> = listOf(),
        var gravity: Vector2Data = Vector2Data()
)

@JsonClass(generateAdapter = true)
data class CameraData(
        var translation: Vector2Data = Vector2Data(),
        var scale: Double = 0.0
)

//helper functions

fun CircleData.createShapeData() = ShapeData(type = SHAPE_TYPE_CIRCLE, circleData = this)
fun RectangleData.createShapeData() = ShapeData(type = SHAPE_TYPE_RECTANGLE, rectangleData = this)
