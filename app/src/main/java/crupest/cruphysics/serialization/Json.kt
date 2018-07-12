package crupest.cruphysics.serialization

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

/**
 * Created by crupest on 2017/12/11.
 * Global moshi object for physics serialization.
 */

fun createDefaultKotlinMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()!!

object JsonParser {
    val moshi = createDefaultKotlinMoshi()
}

inline fun <reified T> T.toJson(): String =
        JsonParser.moshi.adapter<T>(T::class.java).toJson(this)

inline fun <reified T> String.fromJson(): T =
        JsonParser.moshi.adapter<T>(T::class.java).fromJson(this) ?:
                throw JsonDataException("The 'fromJson' return null.")
