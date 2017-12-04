package crupest.cruphysics

/**
 * Created by crupest on 2017/11/30.
 * Interface IOptionMenuActivity
 */
interface IOptionMenuActivity {
    var optionMenu: Int
    val optionMenuItemSelectedEvent: ReturnEvent<OptionMenuItemSelectedEventArgs, Boolean>
}
