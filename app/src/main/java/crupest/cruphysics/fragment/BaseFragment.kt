package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import crupest.cruphysics.IDrawerActivity
import crupest.cruphysics.INavigateBackButtonActivity

abstract class BaseFragment : Fragment() {
    open fun determineShowNavigateBackButton(): Boolean = false
    open fun determineDrawer(activity: IDrawerActivity): Fragment? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = context

        var setBackButton = true

        if (activity is IDrawerActivity) {
            val drawerFragment = determineDrawer(activity)
            activity.setDrawerFragment(drawerFragment)
            setBackButton = drawerFragment == null
        }

        if (setBackButton && activity is INavigateBackButtonActivity)
            activity.setNavigateBackButton(determineShowNavigateBackButton())
    }
}
