package crupest.cruphysics

import androidx.fragment.app.Fragment

interface IFragmentNavigation {
    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true)
    fun popBackStack(): Boolean
    fun getFragment(): Fragment?
}

fun IFragmentNavigation.popBackStackRecursive(): Boolean {
    // if child fragment is a navigation fragment, first do its navigation.
    val fragment = getFragment()
    if (fragment is IFragmentNavigation && fragment.popBackStackRecursive())
        return true
    return popBackStack()
}

fun Fragment.popParentBackStack(): Boolean {
    this.parentFragment.apply {
        if (this is IFragmentNavigation) {
            return this.popBackStack()
        }
    }
    this.context.apply {
        if (this is IFragmentNavigation) {
            return this.popBackStack()
        }
    }

    throw UnsupportedOperationException("No parent back stack.")
}
