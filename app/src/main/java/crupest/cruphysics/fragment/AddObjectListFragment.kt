package crupest.cruphysics.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

import crupest.cruphysics.R


class AddObjectListFragment : Fragment() {
    interface EventListener {
        fun onAddObjectListItemSelected(position: Int)
    }

    private var listener: EventListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_add_object_list, container, false)

        val listView = rootView.findViewById<ListView>(R.id.list)
        val adapter = ArrayAdapter(context, R.layout.add_object_list_item, R.id.content,
                context.resources.getStringArray(R.array.add_object_list))
        listView.adapter = adapter

        listView.setOnItemClickListener(fun(_, _, position, _) {
            listener?.onAddObjectListItemSelected(position)
        })

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is EventListener) {
            listener = context
        }
    }
}
