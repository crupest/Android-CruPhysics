package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import crupest.cruphysics.AddBodyActivity
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.R
import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.physics.serialization.BODY_TYPE_DYNAMIC
import crupest.cruphysics.physics.serialization.BODY_TYPE_STATIC
import crupest.cruphysics.physics.serialization.BodyAppearanceData
import crupest.cruphysics.physics.serialization.BodyData
import crupest.cruphysics.utility.generateRandomColor
import crupest.cruphysics.utility.showAlertDialog

/**
 * Created by crupest on 2017/11/25.
 * Class AddBodyFragment.
 */


abstract class AddBodyFragment(private val layoutId: Int) : Fragment() {

    private lateinit var worldCanvas: AddBodyWorldCanvas

    private fun onOk() {

        fun checkOrThrow(id: Int, propertyName: String, predicate: (Double) -> Boolean): Double {
            val number = view!!.findViewById<EditText>(id).text.toString().toDoubleOrNull()
                    ?: throw RuntimeException("${propertyName.capitalize()} is not a number.")
            if (!predicate(number))
                throw RuntimeException("${propertyName.capitalize()} is not in valid range.")
            return number
        }

        try {
            val density = checkOrThrow(R.id.edit_density, "density") { it > 0.0 }
            val restitution = checkOrThrow(R.id.edit_restitution, "restitution") { it >= 0.0 }
            val friction = checkOrThrow(R.id.edit_friction, "friction") { it >= 0.0 }

            val (shape, position, rotation) = worldCanvas.generateShapeInfo()

            val a = context as AddBodyActivity
            a.setResultAndFinish(BodyData(
                    shape = shape,
                    type = when (view!!.findViewById<Spinner>(R.id.body_type_spinner).selectedItemPosition) {
                        0 -> BODY_TYPE_STATIC
                        1 -> BODY_TYPE_DYNAMIC
                        else -> throw RuntimeException("Unexpected object type spinner selection.")
                    },
                    density = density,
                    restitution = restitution,
                    friction = friction,
                    position = position,
                    rotation = rotation,
                    appearance = BodyAppearanceData(
                            color = worldCanvas.color
                    )
            ), worldCanvas.generateCameraData())
        } catch (e: Exception) {
            showAlertDialog(context!!, e.message.orEmpty())
        }
    }

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

        val initColor = generateRandomColor()
        worldCanvas.color = initColor
        colorBlock.background = ColorDrawable(initColor)

        colorBlock.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle("Choose color:")
                    .initialColor(worldCanvas.color)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("ok") { _, selectedColor, _ ->
                        worldCanvas.color = selectedColor
                        colorBlock.background = ColorDrawable(selectedColor)
                    }
                    .setNegativeButton("cancel") { _, _ -> }
                    .lightnessSliderOnly()
                    .build()
                    .show()
        }

        rootView.findViewById<EditText>(R.id.edit_density).setText("1.0")
        rootView.findViewById<EditText>(R.id.edit_restitution).setText("0.0")
        rootView.findViewById<EditText>(R.id.edit_friction).setText("0.2")

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = context as AddBodyActivity
        worldCanvas.setCamera(activity.cameraData)
        worldCanvas.drawWorldDelegate = activity.worldViewData
    }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.add_object_menu
            activity.optionMenuItemSelectedListener = {
                if (it.itemId == R.id.ok) {
                    onOk()
                    true
                } else false
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

        (activity as AddBodyActivity).cameraData = worldCanvas.generateCameraData()
    }
}
