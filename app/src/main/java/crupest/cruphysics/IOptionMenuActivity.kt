package crupest.cruphysics

import android.view.MenuItem
import androidx.annotation.MenuRes

/**
 * Created by crupest on 2017/11/30.
 * Interface [IOptionMenuActivity].
 * Represents an activity with a settable option menu.
 */
interface IOptionMenuActivity {
    /**
     * Get or set the resource id of the option menu.
     */
    @get:MenuRes
    @setparam:MenuRes
    var optionMenu: Int

    /**
     * Get or set the listener of the menu item selection.
     * It is invoked when a option menu item is selected.
     */
    var optionMenuItemSelectedListener: ((MenuItem) -> Boolean)?
}
