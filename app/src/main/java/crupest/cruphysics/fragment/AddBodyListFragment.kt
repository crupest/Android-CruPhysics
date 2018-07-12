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


class AddBodyListFragment : Fragment() {

    private inner class MyAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        override fun getCount(): Int = 2
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> AddBodyListItemFragment.newInstance(context!!.resources.getString(R.string.circle_body), R.drawable.circle_object_sample)
            1 -> AddBodyListItemFragment.newInstance(context!!.resources.getString(R.string.rectangle_body), R.drawable.rectangle_object_sample)
            else -> throw RuntimeException("Out of range!")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_body_list, container, false)

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
            activity.optionMenuItemSelectedListener = {
                if (it.itemId == R.id.next) {
                    if (activity is SingleFragmentActivity) {
                        val pager = view!!.findViewById<ViewPager>(R.id.pager)
                        when (pager.currentItem) {
                            0 -> activity.navigateToFragment(AddCircleBodyFragment())
                            1 -> activity.navigateToFragment(AddRectangleBodyFragment())
                        }
                    }
                    true
                } else false
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
