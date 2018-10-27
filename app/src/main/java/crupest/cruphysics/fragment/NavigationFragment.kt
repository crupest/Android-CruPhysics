package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import crupest.cruphysics.IFragmentNavigator
import crupest.cruphysics.MainActivity
import crupest.cruphysics.R

abstract class NavigationFragment : BaseFragment(), IFragmentNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            navigateToFragment(onNavigateToFirstFragment(), false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    abstract fun onNavigateToFirstFragment(): BaseFragment

    private fun notifyActivityNavigated() {
        (context as MainActivity).notifyNavigated()
    }

    private fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.navigation_fragment_root, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
            transaction.commit()
        } else
            transaction.commitNow()
        notifyActivityNavigated()
    }

    override fun navigateTo(fragment: BaseFragment) {
        navigateToFragment(fragment, true)
    }

    override fun getCurrentFragment(): BaseFragment =
            (childFragmentManager.findFragmentById(R.id.navigation_fragment_root)
                    ?: throw IllegalStateException("Fragment not loaded.")) as BaseFragment

    override fun popBackStack(): Boolean {
        val result =  childFragmentManager.popBackStackImmediate()
        notifyActivityNavigated()
        return result
    }
}
