package crupest.cruphysics.fragment

abstract class ShapeProperty(val name: String, val inputType: Int) {

    private var listener: ((String) -> Unit)? = null

    abstract val currentValue: String

    /** throw an [ShapePropertyValidationException] when validation fails.
     *  return the string that will be presented.
     */
    abstract fun validateAndSetValue(v: String)

    fun setValueChangedListener(listener: ((String) -> Unit)?) {
        this.listener = listener
    }

    protected fun raiseValueChanged(newValue: String) {
        listener?.invoke(newValue)
    }
}
