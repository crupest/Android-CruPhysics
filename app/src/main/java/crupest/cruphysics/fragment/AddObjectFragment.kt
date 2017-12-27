package crupest.cruphysics.fragment

import android.support.v4.app.Fragment
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.OptionMenuItemSelectedEventArgs
import crupest.cruphysics.R

/**
 * Created by crupest on 2017/11/25.
 * Class AddObjectFragment.
 */


/**
 * An fragment with a option menu containing a "check" menu item.
 */
abstract class AddObjectFragment : Fragment() {

    /**
     * invoked when the "check" menu item in option menu is clicked.
     */
    protected abstract fun onOk()

    private val onOptionMenuItemSelectedEventListener: (OptionMenuItemSelectedEventArgs) -> Boolean = l@ {
        if (it.menuItem.itemId == R.id.ok) {
            onOk()
            return@l true
        }
        return@l false
    }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.add_object_menu
            activity.optionMenuItemSelectedEvent.addListener(onOptionMenuItemSelectedEventListener)
        }
    }

    override fun onPause() {
        super.onPause()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = 0
            activity.optionMenuItemSelectedEvent.removeListener(onOptionMenuItemSelectedEventListener)
        }
    }
}
