package crupest.cruphysics.fragment

import android.support.v4.app.Fragment
import android.view.View
import crupest.cruphysics.FixturePropertyData
import crupest.cruphysics.R
import crupest.cruphysics.component.ObjectPropertyView

/**
 * Created by crupest on 2017/11/25.
 * Class AddObjectFragment.
 */
abstract class AddObjectFragment : Fragment() {
    interface EventListener {
        fun onAddObjectFragmentDetach()
    }

    abstract fun onOk()
    protected abstract val rootView: View?

    protected fun extractFixtureProperty() : FixturePropertyData {
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
}
