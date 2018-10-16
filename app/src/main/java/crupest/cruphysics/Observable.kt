package crupest.cruphysics


class Observable<T>(init: T) {
    var value: T = init
        set(value) {
            field = value
            changeListener?.invoke(value)
        }

    var changeListener: ((T) -> Unit)? = null
}
