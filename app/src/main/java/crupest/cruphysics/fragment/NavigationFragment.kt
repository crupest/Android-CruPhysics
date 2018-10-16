package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import crupest.cruphysics.R

open class NavigationFragment : OptionMenuFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.navigation_fragment_root, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    fun popBackStack(): Boolean {
        val fragment = childFragmentManager.findFragmentById(R.id.navigation_fragment_root)
        if (fragment is NavigationFragment && fragment.popBackStack())
            return true
        return childFragmentManager.popBackStackImmediate()
    }

    fun popBackStackNonRecursive(): Boolean {
        return childFragmentManager.popBackStackImmediate()
    }
}
