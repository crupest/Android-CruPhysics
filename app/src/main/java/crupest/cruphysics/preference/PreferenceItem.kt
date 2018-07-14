package crupest.cruphysics.preference

import android.content.Context
import android.view.View
import crupest.cruphysics.preference.labelview.IPreferenceLabelViewDelegate
import crupest.cruphysics.preference.valueview.IPreferenceValueDelegate


abstract class PreferenceItem<TValue>(
        val context: Context,
        val labelViewDelegate: IPreferenceLabelViewDelegate,
        val valueViewDelegate: IPreferenceValueDelegate<TValue>) {

    abstract fun createView(context: Context): View
}
