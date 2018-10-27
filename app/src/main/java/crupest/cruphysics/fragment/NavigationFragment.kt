package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
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

    private fun navigateToFragment(fragment: BaseFragment, addToBackStack: Boolean) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.navigation_fragment_root, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addToBackStack) {
            transaction.addToBackStack(null)
            transaction.commit()
            childFragmentManager.executePendingTransactions()
            onNavigate(fragment)
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
        onNavigate(getCurrentFragment())
        notifyActivityNavigated()
        return result
    }

    protected open fun onNavigate(newFragment: BaseFragment) {

    }
}
