package crupest.cruphysics.component

/**
 * Created by crupest on 2017/11/25.
 * Exception class FixturePropertyExtractException.
 */
class FixturePropertyExtractException : Exception{
    constructor(property: String) : super("${property.capitalize()} is not valid.")
    constructor(property: String, cause: Throwable)
            : super("${property.capitalize()} is not valid.", cause)
}
