package crupest.cruphysics.utility

import java.lang.ref.WeakReference

fun <T> T.weakReference(): WeakReference<T> = WeakReference(this)
