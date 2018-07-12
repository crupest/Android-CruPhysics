package crupest.cruphysics.fragment

import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.component.AddRectangleObjectWorldCanvas

class AddRectangleBodyFragment : AddBodyFragment() {
    override fun createWorldCanvas(): AddBodyWorldCanvas = AddRectangleObjectWorldCanvas(context, null)
}
