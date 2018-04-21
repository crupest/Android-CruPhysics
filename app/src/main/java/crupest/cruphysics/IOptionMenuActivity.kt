package crupest.cruphysics

import android.view.MenuItem

/**
 * Created by crupest on 2017/11/30.
 * Interface [IOptionMenuActivity].
 * Represents an activity with a settable option menu.
 */
interface IOptionMenuActivity {
    /**
     * Get or set the resource id of the option menu.
     */
    var optionMenu: Int

    /**
     * Get or set the listener of the menu item selection.
     * It is invoked when a option menu item is selected.
     */
    var optionMenuItemSelectedListener: ((MenuItem) -> Boolean)?
}
