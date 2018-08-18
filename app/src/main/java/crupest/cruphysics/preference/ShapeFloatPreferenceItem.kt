package crupest.cruphysics.preference

import android.content.Context
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import crupest.cruphysics.preference.labelview.TextPreferenceLabelViewDelegate
import crupest.cruphysics.preference.valueview.EditTextPreferenceValueDelegate

class ShapeFloatPreferenceItem(
        name: String,
        private val onRestore: () -> Float,
        private val onValueChanged: (Float) -> Boolean,
        signed: Boolean = false
) : IViewDelegate {
    companion object {
        private fun createLayoutParams(): LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }

        val commonViewCreator: IViewCreator = object : IViewCreator {
            override fun createView(context: Context): View {
                val linearLayout = LinearLayout(context)

                val label = TextPreferenceLabelViewDelegate.commonViewCreator.createView(context)
                label.layoutParams = createLayoutParams()
                label.id = labelId
                linearLayout.addView(label)

                val edit = EditTextPreferenceValueDelegate.commonViewCreator.createView(context)
                edit.layoutParams = createLayoutParams()
                edit.id = editId
                linearLayout.addView(edit)

                return linearLayout
            }
        }

        private val labelId = View.generateViewId()
        private val editId = View.generateViewId()

        private fun calculateInputType(signed: Boolean): Int {
            var result = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            if (signed)
                result = (result or InputType.TYPE_NUMBER_FLAG_SIGNED)
            return result
        }

    }

    private val labelDelegate: TextPreferenceLabelViewDelegate = TextPreferenceLabelViewDelegate(name)
    private val valueDelegate: EditTextPreferenceValueDelegate = EditTextPreferenceValueDelegate(
            calculateInputType(signed), {
        onRestore().toString()
    }, {
        val number = it.toFloatOrNull()
        if (number == null)
            false
        else
            onValueChanged(number)
    })

    override val viewCreator: IViewCreator
        get() = commonViewCreator

    override fun bindView(view: View) {
        labelDelegate.bindView(view.findViewById(labelId))
        valueDelegate.bindView(view.findViewById(editId))
    }

    override fun unbindView(view: View) {
        labelDelegate.unbindView(view.findViewById(labelId))
        valueDelegate.unbindView(view.findViewById(editId))
    }

    fun setCurrentValue(value: Float) {
        valueDelegate.setCurrentText(value.toString())
    }
}
