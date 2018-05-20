package crupest.cruphysics.component

import crupest.cruphysics.physics.serialization.ShapeData
import crupest.cruphysics.physics.serialization.Vector2Data

data class ShapeInfo(
        val shapeData: ShapeData,
        val position: Vector2Data,
        val rotation: Double
)
