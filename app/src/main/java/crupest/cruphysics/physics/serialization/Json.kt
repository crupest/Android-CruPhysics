package crupest.cruphysics.physics.serialization

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import crupest.cruphysics.physics.serialization.unmapper.UnmapException

/**
 * Created by crupest on 2017/12/11.
 * Global moshi object for physics serialization.
 */

fun createDefaultKotlinMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()!!

object JsonParser {
    val moshi = createDefaultKotlinMoshi()
    val objectAdapter = moshi.adapter<JsonObject>(Map::class.java)!!
}


fun JsonObject.toJson(): String = JsonParser.objectAdapter.toJson(this)
fun String.parseAsJsonObject(): JsonObject? = JsonParser.objectAdapter.fromJson(this)

inline fun <reified T> T.toJson(): String =
        JsonParser.moshi.adapter<T>(T::class.java).toJson(this)

inline fun <reified T> String.fromJson(): T? =
        JsonParser.moshi.adapter<T>(T::class.java).fromJson(this)

typealias JsonObject = Map<String, Any>
typealias JsonArray = List<Any>

/**
 * Check whether an object is a JsonObject.
 * It first checks whether it is a map and then checks whether all keys are String and all values are Any.
 * @param obj the object to check
 * @return if [obj] is JsonObject return itself, otherwise null.
 */
fun checkJsonObject(obj: Any): JsonObject? {
    if (obj is Map<*, *> && obj.all {
        it.key is String && it.key is Any
    }) {
        @Suppress("UNCHECKED_CAST")
        return obj as JsonObject
    }
    return null
}

/**
 * Check whether an object is a JsonArray.
 * It checks whether it is a list and then checks whether all elements are Any.
 * @param obj the object to check
 * @return if [obj] is JsonArray return itself, otherwise null.
 */
fun checkJsonArray(obj: Any): JsonArray? {
    if (obj is List<*> && obj.all {
        it is Any
    }) {
        @Suppress("UNCHECKED_CAST")
        return obj as JsonArray
    }
    return null
}

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
    return checkJsonObject(property) ?: throw UnmapException("Property \"$name\" is not of type object.")
}

fun JsonObject.getArrayProperty(name: String): JsonArray {
    val property = this[name] ?: throw UnmapException("Property \"$name\" doesn't exist.")
    return checkJsonArray(property) ?: throw UnmapException("Property \"$name\" is not of type array.")
}
