package crupest.cruphysics

import crupest.cruphysics.fragment.BaseFragment

interface IFragmentNavigator {
    fun navigateTo(fragment: BaseFragment)
    fun popBackStack(): Boolean

    fun getCurrentFragment(): BaseFragment
}

fun IFragmentNavigator.popBackStackRecursive(): Boolean {
    // if child fragment is a navigation fragment, first do its navigation.
    val fragment = getCurrentFragment()
    if (fragment is IFragmentNavigator && fragment.popBackStackRecursive())
        return true
    return popBackStack()
}
