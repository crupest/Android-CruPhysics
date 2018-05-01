package crupest.cruphysics.component

import org.dyn4j.dynamics.Body

interface IMainWorldDelegate {
    fun bodyHitTest(x: Double, y: Double): Body?
    fun removeBody(body: Body)
}
