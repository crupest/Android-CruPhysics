package crupest.cruphysics.utility

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.absoluteValue
import kotlin.math.pow

fun EditText.bindDoubleLiveData(
        lifecycleOwner: LifecycleOwner,
        liveData: MutableLiveData<Double>,
        numberPrecision: Int = 6,
        correctAfterLoseFocus: Boolean = true,
        init: Boolean = true,
        noLessThan0: Boolean = false) {

    val numberFormat = NumberFormat.getNumberInstance() as DecimalFormat
    numberFormat.maximumFractionDigits = numberPrecision
    numberFormat.isDecimalSeparatorAlwaysShown = false

    if (init)
        this.setText(numberFormat.format(liveData.value))

    liveData.observe(lifecycleOwner, Observer {
        this.setText(numberFormat.format(it))
    })

    var error = false

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            text.toString().toDoubleOrNull().apply {
                if (this == null || (noLessThan0 && this < 0.0)) {
                    error = true
                    return
                } else {
                    if ((this - (liveData.value ?: 0.0)).absoluteValue < 0.1.pow(numberPrecision))
                        liveData.value = this
                    error = true
                }
            }
        }
    })

    if (correctAfterLoseFocus)
        this.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && error) {
                this.setText(numberFormat.format(liveData.value))
            }
        }
}
