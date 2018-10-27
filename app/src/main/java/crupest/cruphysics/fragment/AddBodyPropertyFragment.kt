package crupest.cruphysics.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.R
import crupest.cruphysics.physics.BodyType
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.bindDoubleLiveData
import crupest.cruphysics.viewmodel.checkAndSetValue
import me.priyesh.chroma.ChromaDialog
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.ColorSelectListener

class AddBodyPropertyFragment : BaseFragment() {

    private lateinit var addBodyViewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        addBodyViewModel = ViewModelProviders.of(parent).get(AddBodyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_body_property, container, false)

        val typeSpinner: Spinner = rootView.findViewById(R.id.body_type_spinner)
        val adapter = ArrayAdapter(context!!, R.layout.item_body_type_spinner, R.id.content,
                context!!.resources.getStringArray(R.array.object_type_list))
        typeSpinner.adapter = adapter


        typeSpinner.setSelection(when (addBodyViewModel.bodyType.value) {
            BodyType.STATIC -> 0
            BodyType.DYNAMIC -> 1
            else -> throw IllegalStateException("Unknown body type.")
        })

        addBodyViewModel.bodyType.observe(this.viewLifecycleOwner, Observer {
            typeSpinner.setSelection(when (addBodyViewModel.bodyType.value) {
                BodyType.STATIC -> 0
                BodyType.DYNAMIC -> 1
                else -> throw IllegalStateException("Unknown body type.")
            })
        })

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> BodyType.STATIC
                    1 -> BodyType.DYNAMIC
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
        bindEditText(R.id.edit_velocity_x, addBodyViewModel.velocityX)
        bindEditText(R.id.edit_velocity_y, addBodyViewModel.velocityY)
        bindEditText(R.id.edit_angular_velocity, addBodyViewModel.angularVelocity)

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

    fun validate(): String? = if (addBodyViewModel.density.value == 0.0) "Density can't be 0." else null
}
