package crupest.cruphysics.fragment

import android.content.Context
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

    private val onOptionMenuItemSelectedEventListener: (OptionMenuItemSelectedEventArgs) -> Unit = {
        if (it.menuItem.itemId == R.id.ok) {
            onOk()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is IOptionMenuActivity) {
            context.optionMenu = R.menu.add_object_menu
            context.optionMenuItemSelectedEvent.addListener(onOptionMenuItemSelectedEventListener)
        }
    }

    override fun onDetach() {
        super.onDetach()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = 0
            activity.optionMenuItemSelectedEvent.removeListener(onOptionMenuItemSelectedEventListener)
        }
    }
}
