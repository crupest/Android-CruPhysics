package crupest.cruphysics

/**
 * Created by crupest on 2017/12/4.
 * Class [ReturnEvent].
 */

/**
 * Event with a return Value.
 */
class ReturnEvent<EventArgs, Ret> {
    private val listeners = mutableListOf<(EventArgs) -> Ret>()

    fun addListener(f: (EventArgs) -> Ret) {
        synchronized(this) {
            listeners.add(f)
        }
    }

    fun removeListener(f: (EventArgs) -> Ret) {
        synchronized(this) {
            listeners.remove(f)
        }
    }

    /**
     * @return value returned by the last listener or [null] if there is no listener
     */
    fun raise(arg: EventArgs) : Ret? {
        var ret : Ret? = null
        synchronized(this) {
            for (listener in listeners)
                ret = listener(arg)
        }
        return ret
    }
}
