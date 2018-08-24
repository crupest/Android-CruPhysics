package crupest.cruphysics.component

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import crupest.cruphysics.R

class ShapePropertyItemViewDelegate(
        val name: String,
        private val onRestore: () -> Double,
        private val onValueChanged: (Double) -> Boolean,
        val signed: Boolean = false
): IViewDelegate {

    private var valueView: EditText? = null
    private var error: Boolean = false

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            error = !onTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup): View {

        fun calculateInputType(): Int {
            var result = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            if (signed)
                result = (result or InputType.TYPE_NUMBER_FLAG_SIGNED)
            return result
        }

        val rootView = inflater.inflate(R.layout.shape_property_item, parent, false)

        rootView.findViewById<TextView>(R.id.label).text = name


        val view = rootView.findViewById<EditText>(R.id.value)

        valueView = view

        view.inputType = calculateInputType()

        view.setText(onRestore().toString())

        view.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && error) restoreText()
        }

        view.addTextChangedListener(textWatcher)

        return rootView
    }

    override fun destroyView(view: View) {
        check(view.findViewById<EditText>(R.id.value) == valueView)
        valueView!!.removeTextChangedListener(textWatcher)
        valueView = null
    }

    private fun onTextChanged(text: String): Boolean {
        val number = text.toDoubleOrNull()
        return if (number == null)
            false
        else
            onValueChanged(number)
    }

    fun restoreText() {
        valueView?.apply {
            removeTextChangedListener(textWatcher)
            setText(onRestore().toString())
            addTextChangedListener(textWatcher)
        }
    }
}
