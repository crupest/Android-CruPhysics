package crupest.cruphysics.utility

/**
 * Created by crupest on 2017/12/13.
 * Collection utilities.
 */

fun <K, V> merge(vararg maps: Map<K, V>) : Map<K, V> {
    val result = mutableMapOf<K, V>()
    for (map in maps) {
        result.putAll(map)
    }
    return result
}
