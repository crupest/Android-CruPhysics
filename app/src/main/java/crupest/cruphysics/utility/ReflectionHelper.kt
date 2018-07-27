package crupest.cruphysics.utility

import kotlin.reflect.KClass

fun fromNameToClass(qualifiedName: String): KClass<*> = Class.forName(qualifiedName).kotlin
