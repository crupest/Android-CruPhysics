package crupest.cruphysics.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import crupest.cruphysics.AddObjectActivity
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.OptionMenuItemSelectedEventArgs

import crupest.cruphysics.R

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

    private val onOptionMenuItemSelectedEventListener: (OptionMenuItemSelectedEventArgs) -> Boolean = l@ {
        if (it.menuItem.itemId == R.id.ok) {
            val activity = context as AddObjectActivity
            activity.navigateToFragment(AddPolygonObjectFragment2.newInstance(
                    numberPicker!!.value
            ))
            return@l true
        }
        return@l false
    }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.add_object_menu
            activity.optionMenuItemSelectedEvent.addListener(onOptionMenuItemSelectedEventListener)
        }
    }

    override fun onPause() {
        super.onPause()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = 0
            activity.optionMenuItemSelectedEvent.removeListener(onOptionMenuItemSelectedEventListener)
        }
    }
}
