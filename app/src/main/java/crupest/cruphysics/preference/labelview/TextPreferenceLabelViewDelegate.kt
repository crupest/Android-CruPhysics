package crupest.cruphysics.preference.labelview

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import crupest.cruphysics.preference.IViewCreator
import crupest.cruphysics.preference.IViewDelegate

open class TextPreferenceLabelViewDelegate(val name: String) : IViewDelegate {

    companion object {
        val commonViewCreator: IViewCreator = object : IViewCreator {
            override fun createView(context: Context): View = TextView(context)
        }
    }

    var setStyleListener: ((TextView) -> Unit)? = null

    final override val viewCreator: IViewCreator
        get() = commonViewCreator

    final override fun bindView(view: View) {
        if (view !is TextView)
            throw IllegalArgumentException("View is not a TextView.")

        setTextViewStyle(view)
        setStyleListener?.invoke(view)
        view.text = name
    }

    final override fun unbindView(view: View) {

    }

    private fun setTextViewStyle(textView: TextView) {
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
    }
}
