package crupest.cruphysics.fragment

import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.component.AddRectangleObjectWorldCanvas

class AddRectangleBodyCanvasFragment : AddBodyCanvasFragment() {
    override fun createWorldCanvas(): AddBodyWorldCanvas = AddRectangleObjectWorldCanvas(context, null)
}
