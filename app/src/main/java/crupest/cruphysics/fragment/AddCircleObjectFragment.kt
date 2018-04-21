package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.*

import crupest.cruphysics.component.AddCircleObjectWorldCanvas
import crupest.cruphysics.component.CommonObjectPropertyView
import crupest.cruphysics.utility.createAlertDialog
import crupest.cruphysics.utility.generateRandomColor


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

        commonObjectPropertyView.color = generateRandomColor()

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


    }
}
