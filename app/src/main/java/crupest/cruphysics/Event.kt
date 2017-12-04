package crupest.cruphysics

/**
 * Created by crupest on 2017/11/4.
 * Class Event and SimpleEvent.
 */

class Event<EventArgs> {
    private val listeners = mutableListOf<(EventArgs) -> Unit>()

    fun addListener(f: (EventArgs) -> Unit) {
        synchronized(this) {
            listeners.add(f)
        }
    }

    fun removeListener(f: (EventArgs) -> Unit) {
        synchronized(this) {
            listeners.remove(f)
        }
    }

    fun raise(arg: EventArgs) {
        synchronized(this) {
            for (listener in listeners)
                listener(arg)
        }
    }
}
