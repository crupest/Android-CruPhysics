package crupest.cruphysics.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import crupest.cruphysics.R
import crupest.cruphysics.serialization.data.SHAPE_TYPE_CIRCLE
import crupest.cruphysics.serialization.data.SHAPE_TYPE_RECTANGLE
import crupest.cruphysics.viewmodel.AddBodyViewModel


class AddBodyShapeListFragment : OptionMenuFragment() {

    class AddBodyShapeListItemFragment : Fragment() {

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
            val rootView = inflater.inflate(R.layout.fragment_add_body_shape_list_item, container, false)

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

            fun newInstance(name: String, imageSrc: Int = 0): AddBodyShapeListItemFragment {
                val fragment = AddBodyShapeListItemFragment()
                val args = Bundle()
                args.putString(ARG_NAME, name)
                args.putInt(ARG_IMAGE_SRC, imageSrc)
                fragment.arguments = args
                return fragment
            }
        }
    }

    init {
        optionMenuRes = R.menu.next_menu
    }

    private inner class MyAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        override fun getCount(): Int = 2
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> AddBodyShapeListItemFragment.newInstance(context!!.resources.getString(R.string.circle_body), R.drawable.circle_body_sample)
            1 -> AddBodyShapeListItemFragment.newInstance(context!!.resources.getString(R.string.rectangle_body), R.drawable.rectangle_body_sample)
            else -> throw RuntimeException("Out of range!")
        }
    }


    private lateinit var addBodyViewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        addBodyViewModel = ViewModelProviders.of(parent).get(AddBodyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_body_shape_list, container, false)

        val viewPager = rootView.findViewById<ViewPager>(R.id.pager)

        val adapter = MyAdapter(childFragmentManager)
        viewPager.adapter = adapter

        return rootView
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.next) {
                val parent = parentFragment as NavigationFragment
                val pager = view!!.findViewById<ViewPager>(R.id.pager)
                when (pager.currentItem) {
                    0 -> {
                        addBodyViewModel.shapeType.value = SHAPE_TYPE_CIRCLE
                        parent.navigateToFragment(AddCircleBodyCanvasFragment())
                    }
                    1 ->{
                        addBodyViewModel.shapeType.value = SHAPE_TYPE_RECTANGLE
                        parent.navigateToFragment(AddRectangleBodyCanvasFragment())
                    }
                }
                true
            } else false
}
