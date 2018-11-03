package crupest.cruphysics.physics

import org.dyn4j.geometry.MassType

enum class BodyType(val massType: MassType) {
    DYNAMIC(MassType.NORMAL),
    STATIC(MassType.INFINITE);

    companion object {
        fun fromMassType(massType: MassType): BodyType =
                values().find { it.massType == massType } ?: throw IllegalArgumentException("MassType($massType) is not support.")
    }
}
