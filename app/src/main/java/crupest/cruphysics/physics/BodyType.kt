package crupest.cruphysics.physics

import org.dyn4j.geometry.MassType

enum class BodyType(val massType: MassType) {
    DYNAMIC(MassType.NORMAL),
    STATIC(MassType.INFINITE)
}
