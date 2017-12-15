package crupest.cruphysics.physics.serialization

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

/**
 * Created by crupest on 2017/12/11.
 * Global moshi object for physics serialization.
 */

val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()!!
val mapper = Mapper()
val unmapper = Unmapper()


typealias JsonObject = Map<String, Any>

fun JsonObject.getStringProperty(name: String): String {
    val property = this[name] ?: throw UnmapException("Property \"$name\" doesn't exist.")

    if (property is String)
        return property
    else
        throw UnmapException("Property \"$name\" is not of type string.")
}

fun JsonObject.getNumberProperty(name: String): Double {
    val property = this[name] ?: throw UnmapException("Property \"$name\" doesn't exist.")

    if (property is Double)
        return property
    else
        throw UnmapException("Property \"$name\" is not of type number.")
}

fun JsonObject.getObjectProperty(name: String): JsonObject {
    val property = this[name] ?: throw UnmapException("Property \"$name\" doesn't exist.")

    @Suppress("UNCHECKED_CAST")
    if (property is Map<*, *>)
        return property as JsonObject
    else
        throw UnmapException("Property \"$name\" is not of type object.")
}
