package crupest.cruphysics

class SimpleEvent {
    private val listeners = mutableListOf<() -> Unit>()

    fun addListener(f: () -> Unit) {
        synchronized(this) {
            listeners.add(f)
        }
    }

    fun removeListener(f: () -> Unit) {
        synchronized(this) {
            listeners.remove(f)
        }
    }

    fun raise() {
        synchronized(this) {
            for (listener in listeners)
                listener()
        }
    }
}
