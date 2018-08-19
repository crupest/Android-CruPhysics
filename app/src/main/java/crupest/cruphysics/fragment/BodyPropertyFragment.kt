package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import crupest.cruphysics.AddBodyActivity
import crupest.cruphysics.R
import crupest.cruphysics.utility.showAlertDialog
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener

class BodyPropertyFragment : OptionMenuFragment(R.menu.check_menu) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_body_property, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val a = context as AddBodyActivity
        val rootView = view!!

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
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.ok) {
                fun extractNumber(id: Int, propertyName: String, validate: (Double) -> Boolean): Double {
                    val number = view!!.findViewById<EditText>(id).text.toString().toDoubleOrNull()
                            ?: throw RuntimeException("${propertyName.capitalize()} is not a number.")
                    if (!validate(number))
                        throw RuntimeException("${propertyName.capitalize()} is not in valid range.")
                    return number
                }

                try {
                    val density = extractNumber(R.id.edit_density, "density") { it > 0.0 }
                    val restitution = extractNumber(R.id.edit_restitution, "restitution") { it >= 0.0 }
                    val friction = extractNumber(R.id.edit_friction, "friction") { it >= 0.0 }


                    val a = context as AddBodyActivity
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
}
