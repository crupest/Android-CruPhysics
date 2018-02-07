package crupest.cruphysics.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crupest.cruphysics.IOptionMenuActivity

import crupest.cruphysics.R
import crupest.cruphysics.SingleFragmentActivity


class AddObjectListFragment : Fragment() {

    private inner class MyAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        override fun getCount(): Int = 3
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> AddObjectListItemFragment.newInstance(context!!.resources.getString(R.string.circle_object), R.drawable.circle_object_sample)
            1 -> AddObjectListItemFragment.newInstance(context!!.resources.getString(R.string.rectangle_object), R.drawable.rectangle_object_sample)
            2 -> AddObjectListItemFragment.newInstance(context!!.resources.getString(R.string.polygon_object), R.drawable.polygon_object_sample)
            else -> throw RuntimeException("Out of range!")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_object_list, container, false)

        val viewPager = rootView.findViewById<ViewPager>(R.id.pager)

        val adapter = MyAdapter(childFragmentManager)
        viewPager.adapter = adapter

        return rootView
    }

    override fun onResume() {
        super.onResume()

        val activity = context
        if (activity is IOptionMenuActivity) {
            activity.optionMenu = R.menu.next_menu
            activity.optionMenuItemSelectedListener = l@ {
                if (it.itemId == R.id.next) {
                    if (activity is SingleFragmentActivity) {
                        val pager = view!!.findViewById<ViewPager>(R.id.pager)
                        when (pager.currentItem) {
                            0 -> activity.navigateToFragment(AddCircleObjectFragment())
                            1 -> activity.navigateToFragment(AddRectangleObjectFragment())
                            2 -> activity.navigateToFragment(AddPolygonObjectFragment1())
                        }
                    }
                    return@l true
                }
                return@l false
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
    }
}
