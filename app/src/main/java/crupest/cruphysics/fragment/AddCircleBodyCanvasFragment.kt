package crupest.cruphysics.fragment

import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.component.AddCircleBodyWorldCanvas
import crupest.cruphysics.physics.serialization.SHAPE_TYPE_CIRCLE

class AddCircleBodyCanvasFragment : AddBodyCanvasFragment() {
    override fun createWorldCanvas(): AddBodyWorldCanvas = AddCircleBodyWorldCanvas(context, null)
    override val shapeType: String
        get() = SHAPE_TYPE_CIRCLE
}
