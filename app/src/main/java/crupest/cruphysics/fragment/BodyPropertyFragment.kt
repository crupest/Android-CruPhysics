package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.AddBodyActivity
import crupest.cruphysics.R
import crupest.cruphysics.serialization.data.BODY_TYPE_DYNAMIC
import crupest.cruphysics.serialization.data.BODY_TYPE_STATIC
import crupest.cruphysics.utility.bindDoubleLiveData
import crupest.cruphysics.utility.showAlertDialog
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.checkAndSetValue
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener

class BodyPropertyFragment : OptionMenuFragment() {

    init {
        optionMenuRes = R.menu.check_menu
    }

    private lateinit var addBodyViewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        addBodyViewModel = ViewModelProviders.of(parent).get(AddBodyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_body_property, container, false)

        val typeSpinner: Spinner = rootView.findViewById(R.id.body_type_spinner)
        val adapter = ArrayAdapter(context!!, R.layout.object_type_spinner_item, R.id.content,
                context!!.resources.getStringArray(R.array.object_type_list))
        typeSpinner.adapter = adapter


        typeSpinner.setSelection(when (addBodyViewModel.bodyType.value) {
            BODY_TYPE_STATIC -> 0
            BODY_TYPE_DYNAMIC -> 1
            else -> throw IllegalStateException("Unknown body type.")
        })

        addBodyViewModel.bodyType.observe(this.viewLifecycleOwner, Observer {
            typeSpinner.setSelection(when (addBodyViewModel.bodyType.value) {
                BODY_TYPE_STATIC -> 0
                BODY_TYPE_DYNAMIC -> 1
                else -> throw IllegalStateException("Unknown body type.")
            })
        })

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> BODY_TYPE_STATIC
                    1 -> BODY_TYPE_DYNAMIC
                    else -> null
                }?.run {
                    addBodyViewModel.bodyType.checkAndSetValue(this)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        fun bindEditText(id: Int, liveData: MutableLiveData<Double>, noLessThan0: Boolean = false) {
            rootView.findViewById<EditText>(id).bindDoubleLiveData(this.viewLifecycleOwner, liveData, noLessThan0 = noLessThan0)
        }


        bindEditText(R.id.edit_density, addBodyViewModel.density, true)
        bindEditText(R.id.edit_restitution, addBodyViewModel.restitution, true)
        bindEditText(R.id.edit_friction, addBodyViewModel.friction, true)


        val colorBlock: View = rootView.findViewById(R.id.color_block)

        colorBlock.background = ColorDrawable(addBodyViewModel.bodyColor.value!!)

        addBodyViewModel.bodyColor.observe(this.viewLifecycleOwner, Observer {
            (colorBlock.background as ColorDrawable).color = it
        })

        colorBlock.setOnClickListener {
            ChromaDialog.Builder()
                    .initialColor((colorBlock.background as ColorDrawable).color)
                    .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                    .onColorSelected(object : ColorSelectListener {
                        override fun onColorSelected(color: Int) {
                            addBodyViewModel.bodyColor.value = color
                        }
                    })
                    .create()
                    .show(childFragmentManager, "ChromaDialog")
        }

        return rootView
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.ok) {
                //TODO!

                val a = context as AddBodyActivity
                val rootView = view!!

                fun validateEditText(id: Int, propertyName: String, validate: (Double) -> Boolean) {
                    val number = rootView.findViewById<EditText>(id).text.toString().toDoubleOrNull()
                            ?: throw RuntimeException("${propertyName.capitalize()} is not a number.")
                    if (!validate(number))
                        throw RuntimeException("${propertyName.capitalize()} is not in valid range.")
                }

                try {
                    validateEditText(R.id.edit_density, "density") { it > 0.0 }
                    validateEditText(R.id.edit_restitution, "restitution") { it >= 0.0 }
                    validateEditText(R.id.edit_friction, "friction") { it >= 0.0 }

                    a.setResultAndFinish()
                } catch (e: Exception) {
                    showAlertDialog(context!!, e.message.orEmpty())
                }
                true
            } else false
}
