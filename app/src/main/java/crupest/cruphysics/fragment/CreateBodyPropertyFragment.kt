package crupest.cruphysics.fragment

import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.viewmodel.BodyPropertyViewModel

class CreateBodyPropertyFragment : BaseBodyPropertyFragment() {
    override fun onSetViewModel(): BodyPropertyViewModel {
        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        return ViewModelProviders.of(parent).get(BodyPropertyViewModel::class.java)
    }
}
