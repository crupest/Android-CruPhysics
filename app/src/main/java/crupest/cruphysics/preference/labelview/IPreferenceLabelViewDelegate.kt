package crupest.cruphysics.preference.labelview

import android.content.Context
import android.view.View

interface IPreferenceLabelViewDelegate {
    fun createLabelView(context: Context): View
}
