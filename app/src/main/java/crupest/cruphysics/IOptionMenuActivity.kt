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

class OptionMenuBuilder {
    val handlers: MutableMap<Int, () -> Unit> = mutableMapOf()

    fun addHandler(id: Int, handler: () -> Unit) {
        handlers[id] = handler
    }
}

fun staticOptionMenu(menuRes: Int, block: OptionMenuBuilder.() -> Unit): IOptionMenuActivity.OptionMenuInfo {
    val builder = OptionMenuBuilder()
    builder.block()

    return IOptionMenuActivity.OptionMenuInfo(Observable(menuRes), builder.handlers)
}

fun dynamicOptionMenu(menuRes: Observable<Int>, block: OptionMenuBuilder.() -> Unit): IOptionMenuActivity.OptionMenuInfo {
    val builder = OptionMenuBuilder()
    builder.block()

    return IOptionMenuActivity.OptionMenuInfo(menuRes, builder.handlers)
}
