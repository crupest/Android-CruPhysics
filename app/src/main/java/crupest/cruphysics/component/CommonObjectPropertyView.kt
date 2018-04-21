package crupest.cruphysics.component

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import crupest.cruphysics.R
import org.dyn4j.geometry.MassType
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import crupest.cruphysics.Event


/**
 * Created by crupest on 2017/12/5.
 * View component class [CommonObjectPropertyView].
 */

class CommonObjectPropertyView : LinearLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    class ColorChangedEventArgs(val newColor: Int)

    val colorChangedEvent = Event<ColorChangedEventArgs>()

    private fun init() {
        orientation = VERTICAL
        RelativeLayout.inflate(context, R.layout.common_object_properties, this)

        val colorBlock = findViewById<View>(R.id.color_block)
        colorBlock.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle("Choose color:")
                    .initialColor(color)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("ok") { _, selectedColor, _ ->
                        color = selectedColor
                    }
                    .setNegativeButton("cancel") { _, _ -> }
                    .lightnessSliderOnly()
                    .build()
                    .show()
        }
    }

    var massType: MassType
        get() = findViewById<ObjectTypeSpinner>(R.id.object_type_spinner).massType
        set(value) {
            findViewById<ObjectTypeSpinner>(R.id.object_type_spinner).massType = value
        }

    var density: Double
        get() {
            val density: Double
            try {
                density = findViewById<ObjectPropertyView>(R.id.density_property_view)
                        .propertyValue.toString().toDouble()
            } catch (e: NumberFormatException) {
                throw FixturePropertyExtractException("density", e)
            }
            if (density <= 0.0)
                throw FixturePropertyExtractException("density")

            return density
        }
        set(value) {
            if (value <= 0.0)
                throw IllegalArgumentException("Density must be greater than 0.")
            findViewById<ObjectPropertyView>(R.id.density_property_view).propertyValue = value.toString()
        }

    var friction: Double
        get() {
            val friction: Double
            try {
                friction = findViewById<ObjectPropertyView>(R.id.friction_property_view).propertyValue.toString().toDouble()
            } catch (e: NumberFormatException) {
                throw FixturePropertyExtractException("friction", e)
            }
            if (friction < 0.0)
                throw FixturePropertyExtractException("friction")

            return friction
        }
        set(value) {
            if (value < 0.0)
                throw IllegalArgumentException("Friction must be greater than 0.")
            findViewById<ObjectPropertyView>(R.id.friction_property_view).propertyValue = value.toString()
        }

    var restitution: Double
        get() {
            val restitution: Double
            try {
                restitution = findViewById<ObjectPropertyView>(R.id.restitution_property_view).propertyValue.toString().toDouble()
            } catch (e: NumberFormatException) {
                throw FixturePropertyExtractException("restitution", e)
            }
            if (restitution < 0.0)
                throw FixturePropertyExtractException("restitution")

            return restitution
        }
        set(value) {
            if (value < 0.0)
                throw IllegalArgumentException("Restitution must be greater than 0.")
            findViewById<ObjectPropertyView>(R.id.restitution_property_view).propertyValue = value.toString()
        }

    var color: Int
        get() {
            val view = findViewById<View>(R.id.color_block)
            return (view.background as ColorDrawable).color
        }
        set(value) {
            val view = findViewById<View>(R.id.color_block)
            view.background = ColorDrawable(value)
            colorChangedEvent.raise(ColorChangedEventArgs(value))
        }
}
