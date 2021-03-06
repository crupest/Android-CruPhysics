package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import crupest.cruphysics.R
import crupest.cruphysics.physics.ShapeType


class CreateBodyShapeListFragment : BaseFragment() {

    class CreateBodyShapeListItemFragment : Fragment() {

        private lateinit var name: String
        private var imageSrc: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            if (arguments != null) {
                name = arguments!!.getString(ARG_NAME)!!
                imageSrc = arguments!!.getInt(ARG_IMAGE_SRC)
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_create_body_shape_list_item, container, false)

            val nameText = rootView.findViewById<TextView>(R.id.name)
            val image = rootView.findViewById<ImageView>(R.id.image)

            nameText.text = name
            image.contentDescription = name

            if (imageSrc == 0)
                image.visibility = View.GONE
            else
                image.setImageResource(imageSrc)

            return rootView
        }

        companion object {
            private const val ARG_NAME = "Name"
            private const val ARG_IMAGE_SRC = "ImageSrc"

            fun newInstance(name: String, imageSrc: Int = 0): CreateBodyShapeListItemFragment {
                val fragment = CreateBodyShapeListItemFragment()
                val args = Bundle()
                args.putString(ARG_NAME, name)
                args.putInt(ARG_IMAGE_SRC, imageSrc)
                fragment.arguments = args
                return fragment
            }
        }
    }

    private inner class MyAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        override fun getCount(): Int = 2
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> CreateBodyShapeListItemFragment.newInstance(context!!.resources.getString(R.string.circle_body), R.drawable.circle_body_sample)
            1 -> CreateBodyShapeListItemFragment.newInstance(context!!.resources.getString(R.string.rectangle_body), R.drawable.rectangle_body_sample)
            else -> throw RuntimeException("Out of range!")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_create_body_shape_list, container, false)

        val viewPager = rootView.findViewById<ViewPager>(R.id.pager)

        val adapter = MyAdapter(childFragmentManager)
        viewPager.adapter = adapter

        return rootView
    }

    fun getCurrentShapeType(): ShapeType = when (view!!.findViewById<ViewPager>(R.id.pager).currentItem) {
        0 -> ShapeType.CIRCLE
        1 -> ShapeType.RECTANGLE
        else -> throw IllegalStateException("Unknown selection.")
    }
}
