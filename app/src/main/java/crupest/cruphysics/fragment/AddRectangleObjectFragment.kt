package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.*

import crupest.cruphysics.component.AddRectangleObjectWorldCanvas
import crupest.cruphysics.component.ObjectTypeSpinner
import crupest.cruphysics.physics.RectangleBodyUserData
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Rectangle

class AddRectangleObjectFragment : AddObjectFragment() {

    override var rootView: View? = null
        private set(value) {
            field = value
        }
    private var worldCanvas: AddRectangleObjectWorldCanvas? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(
                R.layout.fragment_add_rectangle_object, container, false)
        worldCanvas = rootView.findViewById(R.id.world_canvas)

        this.rootView = rootView
        return rootView
    }

    override fun onOk() {
        val rect = worldCanvas!!.worldRect

        if (rect.width == 0.0f) {
            val dialog = createAlertDialog(context, "The width can't be zero.")
            dialog.show()
            return
        }

        if (rect.height == 0.0f) {
            val dialog = createAlertDialog(context, "The height can't be zero.")
            dialog.show()
            return
        }

        val body = Body()
        body.translate(
                rect.centerX.toDouble(),
                rect.centerY.toDouble()
        )
        val rectangle = Rectangle(
                rect.width.toDouble(),
                rect.height.toDouble()
        )
        val fixture = BodyFixture(rectangle)

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
        body.userData = RectangleBodyUserData(body)
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

    override fun onDetach() {
        super.onDetach()

        val activity = context
        if (activity is AddObjectFragment.EventListener) {
            activity.onAddObjectFragmentDetach()
        }
    }
}
