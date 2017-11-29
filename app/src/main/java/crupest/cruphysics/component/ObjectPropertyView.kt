package crupest.cruphysics.component

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView

import crupest.cruphysics.R

/**
 * The edit view of a object's property.
 * It contains a label and edit.
 */
class ObjectPropertyView : RelativeLayout {
    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private var propertyLabel: TextView? = null
    private var propertyEdit: EditText? = null


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.ObjectPropertyView, defStyle, 0
        )

        val propertyLabelText = a.getString(R.styleable.ObjectPropertyView_propertyName) ?: ""
        val propertyValueText = a.getString(R.styleable.ObjectPropertyView_propertyValue) ?: ""
        val inputType = a.getInt(R.styleable.ObjectPropertyView_android_inputType, InputType.TYPE_CLASS_TEXT)

        a.recycle()

        inflate(context, R.layout.object_property_view, this)

        propertyLabel = findViewById(R.id.property_label)
        propertyEdit = findViewById(R.id.property_edit)


        propertyName = propertyLabelText
        propertyValue = propertyValueText
        this.inputType = inputType
    }

    var inputType: Int
        get() = propertyEdit!!.inputType
        set(value) {
            propertyEdit!!.inputType = value
        }

    var propertyName: CharSequence
        get() = propertyLabel!!.text
        set(value) {
            propertyLabel!!.text = value
        }

    var propertyValue: CharSequence
        get() = propertyEdit!!.text
        set(value) {
            propertyEdit!!.setText(value)
        }
}
