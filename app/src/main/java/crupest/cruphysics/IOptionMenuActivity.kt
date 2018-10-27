package crupest.cruphysics

/**
 * Created by crupest on 2017/11/30.
 * Interface [IOptionMenuActivity].
 * Represents an activity with a settable option menu.
 */
interface IOptionMenuActivity {
    class OptionMenuInfo(val menuRes: Observable<Int>, val handlers: Map<Int, () -> Unit>)

    fun setOptionMenu(optionMenuInfo: OptionMenuInfo?)
}

infix fun Int.withHandler(handler: () -> Unit): Pair<Int, () -> Unit> = this to handler

fun staticOptionMenu(menuRes: Int, handlers: Map<Int, () -> Unit>) = IOptionMenuActivity.OptionMenuInfo(
        Observable(menuRes), handlers
)
