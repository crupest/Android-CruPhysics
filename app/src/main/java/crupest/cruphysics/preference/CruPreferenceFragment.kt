package crupest.cruphysics.preference

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import crupest.cruphysics.R
import crupest.cruphysics.preference.valueview.ValidationException
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


class CruPreferenceFragment : Fragment() {

    private inner class PreferenceAdapter(private val list: List<PreferenceItem>)
        : RecyclerView.Adapter<PreferenceAdapter.ViewHolder>() {

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
                    } catch (e: ValidationException) {
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


    private lateinit var preferenceList: List<PreferenceItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cru_preference, container, false)
    }

    companion object {

        private const val ARG_PREFERENCE_LIST = "preference_list"

        /**
         * Create a [CruPreferenceFragment].
         *
         * @param preferenceList a list of classes of preference item, they must be subclasses of [PreferenceItem].
         * @return A new instance of fragment [CruPreferenceFragment].
         */
        fun newInstance(vararg preferenceList: KClass<*>) = CruPreferenceFragment().apply {
            arguments = Bundle().apply {
                for (preferenceItem in preferenceList) {
                    if (!preferenceItem.isSubclassOf(PreferenceItem::class))
                        throw IllegalArgumentException(
                                "The preference item class ${preferenceItem.qualifiedName} is not a subclass of PreferenceItem.")
                }

                putStringArray(ARG_PREFERENCE_LIST, preferenceList.map {
                    it.qualifiedName
                }.toTypedArray())
            }
        }
    }
}
