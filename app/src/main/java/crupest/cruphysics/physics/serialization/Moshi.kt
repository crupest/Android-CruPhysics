package crupest.cruphysics.physics.serialization

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Vector2

/**
 * Created by crupest on 2017/12/11.
 * Global moshi object for physics serialization.
 */

val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()!!

typealias JsonObject = Map<String, Any>

inline fun <reified T> JsonObject.getProperty(name: String): T {
    val property = this[name] ?: throw JsonDataException("Property \"$name\" doesn't exist.")

    if (property is T)
        return property
    else
        throw JsonDataException("Property \"$name\" is not ${T::class.simpleName}")
}

fun BodyFixture.putBodyFixtureProperty(map: MutableMap<String, Any>) {
    map.put("density", this.density)
    map.put("friction", this.friction)
    map.put("restitution", this.restitution)
}

fun Vector2.toJsonObject(): JsonObject = mapOf(
        "x" to this.x,
        "y" to this.y
)
