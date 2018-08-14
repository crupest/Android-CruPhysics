package crupest.cruphysics.preference


/**
 * Represents a mutable value that notifies the listener when changed.
 * My English is not that good so I can't think out a better name.
 */
interface INotifyValue <TValue> {
    /**
     * Called when the value is changed.
     * You can accept it and do sth or reject it while setting a fallback value with
     * [setCurrentValue].
     */
    fun setValueChangedListener(listener: ((TValue) -> Unit)?)

    /**
     * Set the current value without notification.
     */
    fun setCurrentValue(value: TValue)
}
