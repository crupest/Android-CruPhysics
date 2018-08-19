package crupest.cruphysics.fragment

import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.component.AddCircleBodyWorldCanvas

class AddCircleBodyCanvasFragment : AddBodyCanvasFragment() {
    override fun createWorldCanvas(): AddBodyWorldCanvas = AddCircleBodyWorldCanvas(context, null)
}
