package crupest.cruphysics.fragment

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import crupest.cruphysics.FixturePropertyData
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.OptionMenuItemSelectedEventArgs
import crupest.cruphysics.R
import crupest.cruphysics.component.ObjectPropertyView

/**
 * Created by crupest on 2017/11/25.
 * Class AddObjectFragment.
 */
abstract class AddObjectFragment : Fragment() {

    protected abstract fun onOk()
    protected abstract val rootView: View?

    protected fun extractFixtureProperty(): FixturePropertyData {
        val data = FixturePropertyData()

        try {
            data.density = rootView!!.findViewById<ObjectPropertyView>(R.id.density_property_view).propertyValue.toString().toDouble()
        } catch (e: NumberFormatException) {
            throw FixturePropertyExtractException("density")
        }

        if (data.density <= 0.0)
            throw FixturePropertyExtractException("density")

        try {
            data.friction = rootView!!.findViewById<ObjectPropertyView>(R.id.friction_property_view).propertyValue.toString().toDouble()
        } catch (e: NumberFormatException) {
            throw FixturePropertyExtractException("friction")
        }

        if (data.friction < 0.0)
            throw FixturePropertyExtractException("friction")

        try {
            data.restitution = rootView!!.findViewById<ObjectPropertyView>(R.id.restitution_property_view).propertyValue.toString().toDouble()
        } catch (e: NumberFormatException) {
            throw FixturePropertyExtractException("restitution")
        }

        if (data.restitution < 0.0)
            throw FixturePropertyExtractException("restitution")

        return data
    }


    private val onOptionMenuItemSelectedEventListener: (OptionMenuItemSelectedEventArgs) -> Unit = {
        if (it.menuItem.itemId == R.id.ok) {
            onOk()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is IOptionMenuActivity) {
            context.optionMenu = R.menu.add_object_menu
            context.optionMenuItemSelectedEvent.addListener(onOptionMenuItemSelectedEventListener)
        }
    }

    override fun onDetach() {
        super.onDetach()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = 0
            activity.optionMenuItemSelectedEvent.removeListener(onOptionMenuItemSelectedEventListener)
        }
    }
}
