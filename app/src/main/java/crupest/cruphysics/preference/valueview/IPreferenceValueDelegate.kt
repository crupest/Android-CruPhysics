package crupest.cruphysics.preference.valueview

import android.content.Context
import android.view.View

interface IPreferenceValueDelegate<TValue> {
    /**
     *  create the value view.
     */
    fun createValueView(context: Context): View

    /**
     *  get the current value.
     */
    fun getCurrentValue(): TValue

    /**
     *  set the listener when the value is changed.
     */
    fun setValueChangedListener(listener: ((TValue) -> Unit)?)
}
