package crupest.cruphysics.fragment

import android.support.v4.app.Fragment
import crupest.cruphysics.AddObjectActivity
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.R
import crupest.cruphysics.component.CommonObjectPropertyView
import crupest.cruphysics.component.FixturePropertyExtractException
import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.utility.showAlertDialog
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Vector2

/**
 * Created by crupest on 2017/11/25.
 * Class AddObjectFragment.
 */


/**
 * An fragment with a option menu containing a "check" menu item.
 */
abstract class AddObjectFragment : Fragment() {

    abstract fun generateShape(): Convex
    abstract fun getPosition(): Vector2
    abstract fun createBodyUserData(body: Body, color: Int): BodyUserData

    /**
     * invoked when the "check" menu item in option menu is clicked.
     */
    protected open fun onOk() {
        val body = Body()
        body.translate(getPosition())
        val fixture = BodyFixture(generateShape())

        val commonObjectPropertyView = view!!.findViewById<CommonObjectPropertyView>(R.id.common_object_property)

        try {
            fixture.density = commonObjectPropertyView.density
            fixture.friction = commonObjectPropertyView.friction
            fixture.restitution = commonObjectPropertyView.restitution
        } catch (e: FixturePropertyExtractException) {
            showAlertDialog(context!!, e.message!!)
            return
        }

        body.addFixture(fixture)
        body.setMass(commonObjectPropertyView.massType)
        body.userData = createBodyUserData(body, commonObjectPropertyView.color)

        val a = context as AddObjectActivity
        a.viewWorld.world.addBody(body)
        a.setResultAndFinish()
    }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.add_object_menu
            activity.optionMenuItemSelectedListener = l@ {
                if (it.itemId == R.id.ok) {
                    onOk()
                    return@l true
                }
                return@l false
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = 0
            activity.optionMenuItemSelectedListener = null
        }
    }
}
