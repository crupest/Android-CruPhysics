package crupest.cruphysics.physics

import org.dyn4j.dynamics.World

/**
 * Created by crupest on 2017/11/2.
 * EventArgs class WorldStateChangeEventArgs.
 */

class WorldStateChangeEventArgs(val world: World, val newState: Boolean)
