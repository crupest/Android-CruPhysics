package crupest.cruphysics.fragment

import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.component.AddCircleBodyWorldCanvas

class AddCircleBodyFragment : AddBodyFragment() {
    override fun createWorldCanvas(): AddBodyWorldCanvas = AddCircleBodyWorldCanvas(context, null)
}
