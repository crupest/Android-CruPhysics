package crupest.cruphysics

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner

/**
 * Created by crupest on 2017/11/30.
 * Interface [IOptionMenuActivity].
 * Represents an activity with a settable option menu.
 */
interface IOptionMenuActivity {
    fun setOptionMenu(lifecycleOwner: LifecycleOwner, menuRes: Observable<Int>, listener: (MenuItem) -> Boolean)
}
