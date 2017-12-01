package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import crupest.cruphysics.AddObjectActivity

import crupest.cruphysics.R

class AddPolygonObjectFragment1 : AddObjectFragment() {

    private var numberPicker: NumberPicker? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_add_polygon_object_fragment1, container, false)

        val numberPicker = rootView.findViewById<NumberPicker>(R.id.number_picker)
        numberPicker.minValue = 3
        numberPicker.maxValue = 8
        this.numberPicker = numberPicker

        return rootView
    }

    override fun onOk() {
        val activity = context as AddObjectActivity
        activity.navigateToFragment(AddPolygonObjectFragment2.newInstance(
                numberPicker!!.value
        ))
    }
}
