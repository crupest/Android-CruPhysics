package crupest.cruphysics.preference

import android.content.Context
import android.view.View

interface IViewCreator {
    fun createView(context: Context): View
}
