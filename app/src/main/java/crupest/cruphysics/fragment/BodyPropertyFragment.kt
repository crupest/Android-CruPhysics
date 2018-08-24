package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import crupest.cruphysics.AddBodyActivity
import crupest.cruphysics.R
import crupest.cruphysics.serialization.data.BODY_TYPE_DYNAMIC
import crupest.cruphysics.serialization.data.BODY_TYPE_STATIC
import crupest.cruphysics.utility.showAlertDialog
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener
import kotlin.reflect.KMutableProperty0

class BodyPropertyFragment : OptionMenuFragment(R.menu.check_menu) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_body_property, container, false)

        val typeSpinner: Spinner = rootView.findViewById(R.id.body_type_spinner)
        val adapter = ArrayAdapter(context, R.layout.object_type_spinner_item, R.id.content,
                context!!.resources.getStringArray(R.array.object_type_list))
        typeSpinner.adapter = adapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val a = context as AddBodyActivity
        val rootView = view!!

        val typeSpinner: Spinner = rootView.findViewById(R.id.body_type_spinner)

        typeSpinner.setSelection(when (a.bodyProperty.type) {
            BODY_TYPE_STATIC -> 0
            BODY_TYPE_DYNAMIC -> 1
            else -> throw IllegalStateException("Unknown body type.")
        })

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> BODY_TYPE_STATIC
                    1 -> BODY_TYPE_DYNAMIC
                    else -> null
                }?.run {
                    a.bodyProperty.type = this
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        fun initEditText(id: Int, field: KMutableProperty0<Double>, validate: (Double) -> Boolean) {
            val editText = rootView.findViewById<EditText>(id)
            editText.setText(field.get().toString())
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editText.text.toString().toDoubleOrNull()?.takeIf(validate)?.run {
                        field.set(this)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
        }

        initEditText(R.id.edit_density, a.bodyProperty::density) { it > 0 }
        initEditText(R.id.edit_restitution, a.bodyProperty::restitution) { it >= 0 }
        initEditText(R.id.edit_friction, a.bodyProperty::friction) { it >= 0 }

        val colorBlock: View = rootView.findViewById(R.id.color_block)

        colorBlock.background = ColorDrawable(a.bodyProperty.color)

        colorBlock.setOnClickListener {
            ChromaDialog.Builder()
                    .initialColor((colorBlock.background as ColorDrawable).color)
                    .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                    .onColorSelected(object : ColorSelectListener {
                        override fun onColorSelected(color: Int) {
                            colorBlock.background = ColorDrawable(color)
                            a.bodyProperty.color = color
                        }
                    })
                    .create()
                    .show(childFragmentManager, "ChromaDialog")
        }
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.ok) {
                val a = context as AddBodyActivity
                val rootView = view!!

                fun validateEditText(id: Int, propertyName: String, validate: (Double) -> Boolean) {
                    val number = rootView.findViewById<EditText>(id).text.toString().toDoubleOrNull()
                            ?: throw RuntimeException("${propertyName.capitalize()} is not a number.")
                    if (!validate(number))
                        throw RuntimeException("${propertyName.capitalize()} is not in valid range.")
                }

                try {
                    validateEditText(R.id.edit_density, "density") { it > 0.0 }
                    validateEditText(R.id.edit_restitution, "restitution") { it >= 0.0 }
                    validateEditText(R.id.edit_friction, "friction") { it >= 0.0 }

                    a.setResultAndFinish()
                } catch (e: Exception) {
                    showAlertDialog(context!!, e.message.orEmpty())
                }
                true
            } else false
}
