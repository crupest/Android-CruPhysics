package crupest.cruphysics.physics.serialization.unmapper

/**
 * Created by crupest on 2017/12/15.
 * Exception class [UnmapException].
 */
class UnmapException : Exception {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}
