package crupest.cruphysics.component

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.Spinner
import crupest.cruphysics.R
import org.dyn4j.geometry.MassType

/**
 * Created by crupest on 2017/11/22.
 * View component ObjectTypeSpinner.
 */
class ObjectTypeSpinner(context: Context, attributeSet: AttributeSet) : Spinner(context, attributeSet) {
    init {
        val adapter = ArrayAdapter(context, R.layout.object_type_spinner_item, R.id.content,
                context.resources.getStringArray(R.array.object_type_list))
        this.adapter = adapter
        this.setSelection(0)
    }

    val massType: MassType
        get() = when (selectedItemPosition) {
            0 -> MassType.INFINITE
            1 -> MassType.NORMAL
            else -> throw RuntimeException("Unexpected object type spinner selection.")
        }
}
