package crupest.cruphysics.preference

import android.content.Context
import android.view.View

interface IViewDelegate {
    fun createView(context: Context): View
    fun bindView(view: View)
    fun unbindView(view: View)
    val viewType: Int
}
