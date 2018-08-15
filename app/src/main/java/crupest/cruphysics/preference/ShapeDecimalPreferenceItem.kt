package crupest.cruphysics.preference

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class ShapeDecimalPreferenceItem : IViewDelegate {
    companion object {
        val commonViewCreator: IViewCreator = object : IViewCreator {
            override fun createView(context: Context): View {
                val linearLayout = LinearLayout(context)
                linearLayout.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                linearLayout.addView()
                //TODO
            }
        }
    }
}
