package crupest.cruphysics.preference.labelview

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView

open class TextPreferenceLabelViewDelegate(val name: String) : IPreferenceLabelViewDelegate {
    final override fun createLabelView(context: Context): View {
        val view = TextView(context)
        view.text = name
        setTextViewStyle(view)
        return view
    }

    protected open fun setTextViewStyle(textView: TextView) {
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
    }
}
