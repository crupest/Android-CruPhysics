package crupest.cruphysics.fragment

import android.support.annotation.MenuRes
import android.support.v4.app.Fragment
import android.view.MenuItem
import crupest.cruphysics.IOptionMenuActivity

abstract class OptionMenuFragment(@MenuRes val menuResource: Int) : Fragment() {
    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = menuResource
            activity.optionMenuItemSelectedListener = ::onOptionMenuItemSelected
        }
    }

    override fun onPause() {
        super.onPause()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = 0
            activity.optionMenuItemSelectedListener = null
        }
    }

    protected open fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean = false
}
