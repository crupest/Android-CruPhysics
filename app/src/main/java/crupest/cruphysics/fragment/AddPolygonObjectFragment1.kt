package crupest.cruphysics.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import crupest.cruphysics.AddObjectActivity
import crupest.cruphysics.IOptionMenuActivity

import crupest.cruphysics.R
import crupest.cruphysics.SingleFragmentActivity

class AddPolygonObjectFragment1 : Fragment() {

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

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.next_menu
            activity.optionMenuItemSelectedListener = l@ {
                if (it.itemId == R.id.next) {
                    if (activity is SingleFragmentActivity)
                        activity.navigateToFragment(AddPolygonObjectFragment2.newInstance(
                                numberPicker!!.value
                        ))
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
