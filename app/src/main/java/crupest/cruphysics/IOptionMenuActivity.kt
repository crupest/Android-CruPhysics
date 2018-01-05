package crupest.cruphysics

import android.view.MenuItem

/**
 * Created by crupest on 2017/11/30.
 * Interface IOptionMenuActivity
 */
interface IOptionMenuActivity {
    var optionMenu: Int
    var optionMenuItemSelectedListener: ((MenuItem) -> Boolean)?
}
