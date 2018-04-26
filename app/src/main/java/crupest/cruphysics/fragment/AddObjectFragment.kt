package crupest.cruphysics.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import crupest.cruphysics.AddObjectActivity
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.R
import crupest.cruphysics.component.AddObjectWorldCanvas
import crupest.cruphysics.utility.showAlertDialog

/**
 * Created by crupest on 2017/11/25.
 * Class AddObjectFragment.
 */


/**
 * An fragment with a option menu containing a "check" menu item.
 */
abstract class AddObjectFragment(private val layoutId: Int) : Fragment() {

    private lateinit var worldCanvas: AddObjectWorldCanvas

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(layoutId, container, false)
        worldCanvas = rootView.findViewById(R.id.world_canvas)

        val typeSpinner: Spinner = rootView.findViewById(R.id.body_type_spinner)
        val adapter = ArrayAdapter(context, R.layout.object_type_spinner_item, R.id.content,
                context!!.resources.getStringArray(R.array.object_type_list))
        typeSpinner.adapter = adapter
        typeSpinner.setSelection(0)


        val colorBlock: View = rootView.findViewById(R.id.color_block)
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
        //TODO: finish the view init code
        return rootView
    }

    /**
     * Backup: TODO
    get() = when (selectedItemPosition) {
    0 -> MassType.INFINITE
    1 -> MassType.NORMAL
    else -> throw RuntimeException("Unexpected object type spinner selection.")
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
     */


    //TODO: add shape generation exception handling code

    /**
     * invoked when the "check" menu item in option menu is clicked.
     */
    protected open fun onOk() {
        val body = Body()
        body.translate(getPosition())
        val fixture = BodyFixture(generateShape())

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
        body.userData = createBodyUserData(body, commonObjectPropertyView.color)

        val a = context as AddObjectActivity
        a.viewWorld.world.addBody(body)
        a.setResultAndFinish()
    }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.add_object_menu
            activity.optionMenuItemSelectedListener = l@ {
                if (it.itemId == R.id.ok) {
                    onOk()
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
