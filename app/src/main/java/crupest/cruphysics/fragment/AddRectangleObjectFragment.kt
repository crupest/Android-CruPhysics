package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.*
import crupest.cruphysics.component.AddRectangleObjectWorldCanvas
import crupest.cruphysics.component.CommonObjectPropertyView
import crupest.cruphysics.component.FixturePropertyExtractException
import crupest.cruphysics.physics.RectangleBodyUserData
import crupest.cruphysics.utility.createAlertDialog
import crupest.cruphysics.utility.getRandomColor
import crupest.cruphysics.utility.showAlertDialog
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Rectangle

class AddRectangleObjectFragment : AddObjectFragment() {

    private lateinit var worldCanvas: AddRectangleObjectWorldCanvas

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(
                R.layout.fragment_add_rectangle_object, container, false)
        worldCanvas = rootView.findViewById(R.id.world_canvas)

        val commonObjectPropertyView = rootView.findViewById<CommonObjectPropertyView>(R.id.common_object_property)
        commonObjectPropertyView.colorChangedEvent.addListener {
            worldCanvas.color = it.newColor
        }

        commonObjectPropertyView.color = getRandomColor()

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val a = context as AddObjectActivity
        worldCanvas.viewWorld = a.viewWorld
    }

    override fun onOk() {
        val rect = worldCanvas.worldRect

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

        val commonObjectPropertyView = view!!.findViewById<CommonObjectPropertyView>(R.id.common_object_property)
        try {
            fixture.density = commonObjectPropertyView.density
            fixture.friction = commonObjectPropertyView.friction
            fixture.restitution = commonObjectPropertyView.restitution
        } catch (e: FixturePropertyExtractException) {
            showAlertDialog(context, e.message!!)
            return
        }

        body.addFixture(fixture)
        body.setMass(commonObjectPropertyView.massType)
        body.userData = RectangleBodyUserData(body, color = commonObjectPropertyView.color)

        val a = context as AddObjectActivity
        a.viewWorld.world.addBody(body)
        a.setResultAndFinish()
    }
}
