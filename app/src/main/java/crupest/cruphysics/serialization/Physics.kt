package crupest.cruphysics.serialization

import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.physics.checkAndGetFixture
import crupest.cruphysics.physics.cruUserData
import crupest.cruphysics.physics.switchShapeR
import crupest.cruphysics.serialization.data.*
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.*
import java.security.InvalidParameterException

fun Vector2.toData(): Vector2Data = Vector2Data(this.x, this.y)

fun Vector2Data.fromData(): Vector2 = Vector2(this.x, this.y)

fun MassType.serializeAsString(): String = when (this) {
    MassType.NORMAL -> BODY_TYPE_DYNAMIC
    MassType.INFINITE -> BODY_TYPE_STATIC
    else -> throw UnsupportedOperationException("MassType $this is unsupported.")
}

fun String.deserializeAsMassType(): MassType = when (this) {
    BODY_TYPE_STATIC -> MassType.INFINITE
    BODY_TYPE_DYNAMIC -> MassType.NORMAL
    else -> throw InvalidParameterException("Invalid string as body type.")
}

fun Shape.toData(): ShapeData = this.switchShapeR {
    onCircle {
        CircleData(
                center = it.center.toData(),
                radius = it.radius
        ).createShapeData()
    }
    onRectangle {
        RectangleData(
                center = it.center.toData(),
                width = it.width,
                height = it.height
        ).createShapeData()
    }
}


fun ShapeData.fromData(): Convex {
    when (this.type) {
        SHAPE_TYPE_CIRCLE -> {
            requireNotNull(this.circleData)
            this.circleData.run {
                return Circle(this.radius).also {
                    it.translate(this.center.x, this.center.y)
                }
            }

        }
        SHAPE_TYPE_RECTANGLE -> {
            requireNotNull(this.rectangleData)
            this.rectangleData.run {
                return Rectangle(this.width, this.height).also {
                    it.translate(this.center.x, this.center.y)
                }
            }
        }
        else -> throw IllegalArgumentException("Invalid ShapeData object.")
    }
}

fun Body.toData(): BodyData {
    val fixture = this.checkAndGetFixture()
    val userData = this.cruUserData

    return BodyData(
            shape = fixture.shape.toData(),
            type = this.mass.type.serializeAsString(),
            position = this.transform.translation.toData(),
            rotation = this.transform.rotation,
            linearVelocity = this.linearVelocity.toData(),
            angularVelocity = this.angularVelocity,
            density = fixture.density,
            restitution = fixture.restitution,
            friction = fixture.friction,
            appearance = BodyAppearanceData(
                    color = userData.color
            )
    )
}

fun BodyData.fromData(): Body {
    val shape = this.shape.fromData()
    val fixture = BodyFixture(shape).also {
        it.density = this.density
        it.restitution = this.restitution
        it.friction = this.friction
    }

    return Body().also {
        it.rotate(this.rotation)
        it.translate(this.position.x, this.position.y)
        it.setLinearVelocity(this.linearVelocity.x, this.linearVelocity.y)
        it.angularVelocity = this.angularVelocity
        it.addFixture(fixture)
        it.setMass(this.type.deserializeAsMassType())
        it.userData = BodyUserData(it, color = this.appearance.color)
    }
}

fun World.toData(): WorldData = WorldData(
        bodies = this.bodies.map { it.toData() },
        gravity = this.gravity.toData()
)

fun WorldData.fromData(world: World) {
    world.gravity = this.gravity.fromData()
    this.bodies.forEach {
        world.addBody(it.fromData())
    }
}
