package crupest.cruphysics.preference

import android.view.View

interface IViewDelegate {
    val viewCreator: IViewCreator
    fun bindView(view: View)
    fun unbindView(view: View)
}
