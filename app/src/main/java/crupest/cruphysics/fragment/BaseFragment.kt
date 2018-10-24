package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import crupest.cruphysics.INavigateBackButtonActivity

abstract class BaseFragment : Fragment() {
    open fun determineShowNavigateBackButton(): Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = context
        if (activity is INavigateBackButtonActivity)
            activity.setNavigateBackButton(determineShowNavigateBackButton())
    }
}
