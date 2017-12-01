package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import crupest.cruphysics.R


class AddPolygonObjectFragment2 : AddObjectFragment() {

    private var sideCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            sideCount = arguments.getInt(ARG_SIDE_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_add_polygon_object_fragment2, container, false)
    }

    override fun onOk() {
        //TODO!!!
    }

    companion object {
        private val ARG_SIDE_COUNT = "SideCount"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param sideCount the number of sides.
         * @return A new instance of fragment AddPolygonObjectFragment2.
         */
        fun newInstance(sideCount: Int): AddPolygonObjectFragment2 {
            val fragment = AddPolygonObjectFragment2()
            val args = Bundle()
            args.putInt(ARG_SIDE_COUNT, sideCount)
            fragment.arguments = args
            return fragment
        }
    }
}
