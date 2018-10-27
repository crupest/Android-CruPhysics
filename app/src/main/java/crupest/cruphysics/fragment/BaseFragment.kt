package crupest.cruphysics.fragment

import androidx.fragment.app.Fragment
import crupest.cruphysics.IDrawerActivity
import crupest.cruphysics.IFragmentNavigator
import crupest.cruphysics.INavigationButtonActivity
import crupest.cruphysics.IOptionMenuActivity

abstract class BaseFragment : Fragment() {
    open fun determineNavigationButton(): INavigationButtonActivity.Button? = null
    open fun determineDrawer(activity: IDrawerActivity): Fragment? = null
    open fun determineOptionMenu(): IOptionMenuActivity.OptionMenuInfo? = null

    fun getParentNavigator(): IFragmentNavigator =
            (parentFragment as? IFragmentNavigator) ?: context as IFragmentNavigator
}
