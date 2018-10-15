package crupest.cruphysics.fragment

import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import crupest.cruphysics.IOptionMenuActivity

abstract class OptionMenuFragment : Fragment() {

    @MenuRes
    var optionMenuRes: Int = 0
        set(value) {
            field = value
            val activity = context
            if (activity is IOptionMenuActivity) {
                activity.optionMenu = value
            }
        }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = optionMenuRes
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
