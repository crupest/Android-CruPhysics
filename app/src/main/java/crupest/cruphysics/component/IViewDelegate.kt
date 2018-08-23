package crupest.cruphysics.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface IViewDelegate {
    fun createView(inflater: LayoutInflater, parent: ViewGroup): View
    fun destroyView(view: View)
}
