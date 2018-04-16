package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.AddObjectActivity
import crupest.cruphysics.R
import crupest.cruphysics.component.AddPolygonObjectWorldCanvas
import crupest.cruphysics.component.CommonObjectPropertyView
import crupest.cruphysics.component.FixturePropertyExtractException
import crupest.cruphysics.physics.PolygonBodyUserData
import crupest.cruphysics.utility.generateRandomColor
import crupest.cruphysics.utility.showAlertDialog
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.geometry.Polygon


class AddPolygonObjectFragment2 : AddObjectFragment() {

    private var sideCount: Int = 0
    private lateinit var worldCanvas: AddPolygonObjectWorldCanvas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            sideCount = arguments!!.getInt(ARG_SIDE_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_polygon_object_fragment2, container, false)

        worldCanvas = rootView.findViewById(R.id.world_canvas)
        worldCanvas.sideCount = sideCount

        val commonObjectPropertyView = rootView.findViewById<CommonObjectPropertyView>(R.id.common_object_property)
        commonObjectPropertyView.colorChangedEvent.addListener {
            worldCanvas.color = it.newColor
        }

        commonObjectPropertyView.color = generateRandomColor()

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val a = context as AddObjectActivity
        worldCanvas.viewWorld = a.viewWorld
    }

    override fun onOk() {
        val rootView = view!!
        val worldCanvas = rootView.findViewById<AddPolygonObjectWorldCanvas>(R.id.world_canvas)

        val shape: Polygon?
        try {
            shape = worldCanvas.generatePolygon()
        } catch (e: IllegalArgumentException) {
            showAlertDialog(context!!, e.message.orEmpty())
            return
        }

        val body = Body()
        val fixture = BodyFixture(shape)

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
        body.userData = PolygonBodyUserData(body, color = commonObjectPropertyView.color)

        val a = context as AddObjectActivity
        a.viewWorld.world.addBody(body)
        a.setResultAndFinish()
    }

    companion object {
        private const val ARG_SIDE_COUNT = "SideCount"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param sideCount the number of sides.
         * @return A new instance of fragment AddPolygonObjectFragment2.
         */
        fun newInstance(sideCount: Int): AddPolygonObjectFragment2 {
            val fragment = AddPolygonObjectFragment2()
            val args = Bundle()
            args.putInt(ARG_SIDE_COUNT, sideCount)
            fragment.arguments = args
            return fragment
        }
    }
}
