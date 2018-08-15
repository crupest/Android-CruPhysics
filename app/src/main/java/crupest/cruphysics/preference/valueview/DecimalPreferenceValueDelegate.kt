package crupest.cruphysics.preference.valueview

import android.content.Context
import android.text.InputType

class DecimalPreferenceValueDelegate(
        context: Context,
        signed: Boolean = true,
        notifyTrigger: NotifyTrigger = NotifyTrigger.ON_LOST_FOCUS
) : TextBasePreferenceValueDelegate<Double>(context, calculateInputType(signed), notifyTrigger) {

    companion object {
        private fun calculateInputType(signed: Boolean) : Int {
            var result = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            if (signed)
                result = (result or InputType.TYPE_NUMBER_FLAG_SIGNED)
            return result
        }
    }

    private var number: Double = 0.0

    override fun onRestoreText(): String = number.toString()

    override fun onTextChanged(text: String) {
            text.toDoubleOrNull()?.apply {
                if (number != this) {
                    number = this
                    raiseValueChanged(number)
                }
            }
    }

    override fun setCurrentValue(value: Double) {
        setCurrentText(value.toString())
    }
}
