package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import crupest.cruphysics.IFragmentNavigation
import crupest.cruphysics.R

open class NavigationFragment : Fragment(), IFragmentNavigation {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.navigation_fragment_root, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun getFragment(): Fragment? {
        return childFragmentManager.findFragmentById(R.id.navigation_fragment_root)
    }

    override fun popBackStack(): Boolean {
        return childFragmentManager.popBackStackImmediate()
    }
}
