package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import crupest.cruphysics.AddBodyActivity
import crupest.cruphysics.R
import crupest.cruphysics.physics.serialization.BODY_TYPE_DYNAMIC
import crupest.cruphysics.physics.serialization.BODY_TYPE_STATIC
import crupest.cruphysics.utility.showAlertDialog
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener

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
        typeSpinner.setSelection(when (a.resultBodyData.type) {
            BODY_TYPE_STATIC -> 0
            BODY_TYPE_DYNAMIC -> 1
            else -> throw IllegalStateException("Unknown body type.")
        })

        rootView.findViewById<EditText>(R.id.edit_density).setText(a.resultBodyData.density.toString())
        rootView.findViewById<EditText>(R.id.edit_restitution).setText(a.resultBodyData.restitution.toString())
        rootView.findViewById<EditText>(R.id.edit_friction).setText(a.resultBodyData.friction.toString())

        val colorBlock: View = rootView.findViewById(R.id.color_block)

        colorBlock.background = ColorDrawable(a.resultBodyData.appearance.color)

        colorBlock.setOnClickListener {
            ChromaDialog.Builder()
                    .initialColor((colorBlock.background as ColorDrawable).color)
                    .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                    .onColorSelected(object : ColorSelectListener {
                        override fun onColorSelected(color: Int) {
                            colorBlock.background = ColorDrawable(color)
                            a.resultBodyData.appearance.color = color
                        }
                    })
                    .create()
                    .show(childFragmentManager, "ChromaDialog")
        }
    }

    override fun onPause() {
        super.onPause()

        val a = context as AddBodyActivity
        val rootView = view!!

        fun extractNumber(id: Int, validate: (Double) -> Boolean): Double? {
            val number = view!!.findViewById<EditText>(id).text.toString().toDoubleOrNull()
            if (number != null && validate(number))
                return number
            return null
        }

        extractNumber(R.id.edit_density) { it > 0.0 }?.run {
            a.resultBodyData.density = this
        }
        extractNumber(R.id.edit_restitution) { it >= 0.0 }?.run {
            a.resultBodyData.restitution = this
        }
        extractNumber(R.id.edit_friction) { it >= 0.0 }?.run {
            a.resultBodyData.friction = this
        }

        rootView.findViewById<Spinner>(R.id.body_type_spinner).getBodyType()?.run {
            a.resultBodyData.type = this
        }
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.ok) {
                val a = context as AddBodyActivity
                val rootView = view!!

                fun extractNumber(id: Int, propertyName: String, validate: (Double) -> Boolean): Double {
                    val number = rootView.findViewById<EditText>(id).text.toString().toDoubleOrNull()
                            ?: throw RuntimeException("${propertyName.capitalize()} is not a number.")
                    if (!validate(number))
                        throw RuntimeException("${propertyName.capitalize()} is not in valid range.")
                    return number
                }

                (rootView.findViewById<Spinner>(R.id.body_type_spinner).getBodyType()
                        ?: throw IllegalStateException("Spinner error selection.")).run {
                    a.resultBodyData.type = this
                }

                try {
                    val density = extractNumber(R.id.edit_density, "density") { it > 0.0 }
                    val restitution = extractNumber(R.id.edit_restitution, "restitution") { it >= 0.0 }
                    val friction = extractNumber(R.id.edit_friction, "friction") { it >= 0.0 }

                    a.resultBodyData.apply {
                        this.density = density
                        this.restitution = restitution
                        this.friction = friction
                    }

                    a.setResultAndFinish()
                } catch (e: Exception) {
                    showAlertDialog(context!!, e.message.orEmpty())
                }
                true
            } else false

    private fun Spinner.getBodyType(): String? = when (this.selectedItemPosition) {
        0 -> BODY_TYPE_STATIC
        1 -> BODY_TYPE_DYNAMIC
        else -> null
    }
}
