package crupest.cruphysics.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.*

import crupest.cruphysics.component.AddCircleObjectWorldCanvas
import crupest.cruphysics.component.CommonObjectPropertyView
import crupest.cruphysics.component.FixturePropertyExtractException
import crupest.cruphysics.physics.CircleBodyUserData
import crupest.cruphysics.physics.toVec2
import crupest.cruphysics.utility.createAlertDialog
import crupest.cruphysics.utility.getRandomColor
import crupest.cruphysics.utility.showAlertDialog
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Circle


class AddCircleObjectFragment : AddObjectFragment() {

    private lateinit var worldCanvas: AddCircleObjectWorldCanvas

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
                R.layout.fragment_add_circle_object, container, false)
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
        val radius = worldCanvas.worldRadius
        if (radius == 0.0f) {
            val dialog = createAlertDialog(context!!, "Radius can't be zero.")
            dialog.show()
            return
        }

        val body = Body()
        body.translate(worldCanvas.worldCenter.toVec2())
        val circle = Circle(radius.toDouble())
        val fixture = BodyFixture(circle)

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
        body.userData = CircleBodyUserData(body, color = commonObjectPropertyView.color)

        val a = context as AddObjectActivity
        a.viewWorld.world.addBody(body)
        a.setResultAndFinish()
    }
}
