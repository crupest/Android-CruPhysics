package crupest.cruphysics.preference.valueview

import android.text.InputType

class DecimalPreferenceValueDelegate(
        val signed: Boolean = true,
        notifyTrigger: NotifyTrigger = NotifyTrigger.ON_LOST_FOCUS
) : TextBasePreferenceValueDelegate<Double>(calculateInputType(signed), notifyTrigger) {

    companion object {
        private fun calculateInputType(signed: Boolean) : Int {
            var result = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            if (signed)
                result = (result or InputType.TYPE_NUMBER_FLAG_SIGNED)
            return result
        }
    }


}
