package crupest.cruphysics.preference

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.utility.checkState


class PreferenceAdapter(val context: Context, private val preferenceList: List<IViewDelegate>)
    : RecyclerView.Adapter<PreferenceAdapter.ViewHolder>() {

    private val viewCreatorList: List<IViewCreator> = preferenceList.map {
        it.viewCreator
    }.distinct()

    override fun getItemViewType(position: Int): Int =
            viewCreatorList.indexOf(preferenceList[position].viewCreator)
                    .checkState("View creator is not found in list.") { it != -1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(viewCreatorList[viewType].createView(context))

    override fun getItemCount(): Int = preferenceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        preferenceList[position].bindView(holder.itemView)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        preferenceList[holder.adapterPosition].unbindView(holder.itemView)
    }

    inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)
}
