package crupest.cruphysics.preference

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.R
import crupest.cruphysics.preference.valueview.ValidationException
import crupest.cruphysics.utility.showAlertDialog


abstract class CruPreferenceFragment : Fragment() {

    private inner class PreferenceAdapter : RecyclerView.Adapter<PreferenceAdapter.ViewHolder>() {

        override fun getItemViewType(position: Int): Int = preferenceList[position].typeId

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
                this.ViewHolder()

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

        inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)
    }


    private lateinit var viewCreateMethodMap: Map<Int, (Context) -> View>
    private lateinit var preferenceList: List<IPreferenceItem>
    private lateinit var adapter: PreferenceAdapter

    protected abstract fun onCreatePreferenceList(): List<IPreferenceItem>

    private fun createPreferenceList() {
        preferenceList = onCreatePreferenceList()

        preferenceList.distinctBy {
            it.typeId
        }.associate {
            it.typeId to it::createView
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cru_preference, container, false) as RecyclerView
        adapter = PreferenceAdapter()
        view.adapter = adapter
        return view
    }
}
