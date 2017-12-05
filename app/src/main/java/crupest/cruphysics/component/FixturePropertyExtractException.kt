package crupest.cruphysics.component

/**
 * Created by crupest on 2017/11/25.
 * Exception class FixturePropertyExtractException.
 */
class FixturePropertyExtractException(val property: String) : Exception("${property.capitalize()} is not valid.")
