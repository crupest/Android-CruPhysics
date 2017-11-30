package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.*

import crupest.cruphysics.component.AddCircleObjectWorldCanvas
import crupest.cruphysics.component.ObjectTypeSpinner
import crupest.cruphysics.physics.CircleBodyUserData
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Circle


class AddCircleObjectFragment : AddObjectFragment() {

    override var rootView: View? = null
        private set(value) {
            field = value
        }
    private var worldCanvas: AddCircleObjectWorldCanvas? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(
                R.layout.fragment_add_circle_object, container, false)
        worldCanvas = rootView.findViewById(R.id.world_canvas)

        this.rootView = rootView
        return rootView
    }

    override fun onOk() {
        val radius = worldCanvas!!.worldRadius
        if (radius == 0.0f) {
            val dialog = createAlertDialog(context, "Radius can't be zero.")
            dialog.show()
            return
        }

        val body = Body()
        body.translate(worldCanvas!!.worldCenter.toVec2())
        val circle = Circle(radius.toDouble())
        val fixture = BodyFixture(circle)

        try {
            val data = extractFixtureProperty()
            fixture.density = data.density
            fixture.friction = data.friction
            fixture.restitution = data.restitution
        } catch (e: FixturePropertyExtractException) {
            showAlertDialog(context, e.message!!)
            return
        }

        body.addFixture(fixture)
        body.setMass(rootView!!.findViewById<ObjectTypeSpinner>(R.id.object_type_spinner).massType)
        body.userData = CircleBodyUserData(body)
        WorldManager.world.addBody(body)
        activity.finish()
    }

    override fun onResume() {
        super.onResume()

        worldCanvas!!.myMatrix.set(WorldManager.viewMatrix)
        worldCanvas!!.invalidate()

    }

    override fun onPause() {
        super.onPause()

        WorldManager.viewMatrix.set(worldCanvas!!.myMatrix)
    }
}
