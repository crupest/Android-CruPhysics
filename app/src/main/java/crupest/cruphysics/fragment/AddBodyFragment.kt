package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener

/**
 * Created by crupest on 2017/11/25.
 * Class AddBodyFragment.
 */


abstract class AddBodyFragment : Fragment() {

    private inner class ShapePropertyAdapter(private val list: List<ShapeProperty>)
        : RecyclerView.Adapter<ShapePropertyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val rootView = LayoutInflater.from(context).inflate(R.layout.shape_property_item, parent, false)
            return ViewHolder(rootView)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val shapeProperty = list[position]
            holder.labelTextView.text = shapeProperty.name

            val valueEditText = holder.valueEditText

            valueEditText.inputType = shapeProperty.inputType
            valueEditText.setText(shapeProperty.currentValue)
            valueEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        shapeProperty.validateAndSetValue(valueEditText.text.toString())
                    } catch (e: ShapePropertyValidationException) {
                        // If validation fails, show an alert dialog and set text as the fallback one.
                        e.message?.also { showAlertDialog(context!!, it) }
                        valueEditText.setText(e.fallbackText)
                    }
                }
            }
            shapeProperty.setValueChangedListener {
                valueEditText.setText(it)
            }
        }

        override fun onViewRecycled(holder: ViewHolder) {
            list[holder.adapterPosition].setValueChangedListener(null)
        }

        inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            val labelTextView: TextView = rootView.findViewById(R.id.label)
            val valueEditText: EditText = rootView.findViewById(R.id.value)
        }
    }



    private lateinit var worldCanvas: AddBodyWorldCanvas

    protected abstract fun createWorldCanvas(): AddBodyWorldCanvas

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
        val rootView = inflater.inflate(R.layout.fragment_add_body, container, false)
        worldCanvas = createWorldCanvas()
        worldCanvas.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val worldCanvasLayout = rootView.findViewById<FrameLayout>(R.id.world_canvas_layout)
        worldCanvasLayout.addView(worldCanvas)

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
            ChromaDialog.Builder()
                    .initialColor(worldCanvas.color)
                    .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                    .onColorSelected(object : ColorSelectListener {
                        override fun onColorSelected(color: Int) {
                            worldCanvas.color = color
                            colorBlock.background = ColorDrawable(color)
                        }
                    })
                    .create()
                    .show(childFragmentManager, "ChromaDialog")
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
