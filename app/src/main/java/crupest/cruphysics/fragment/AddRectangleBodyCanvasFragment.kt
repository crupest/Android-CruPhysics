package crupest.cruphysics.fragment

import crupest.cruphysics.component.AddBodyWorldCanvas
import crupest.cruphysics.component.AddRectangleObjectWorldCanvas
import crupest.cruphysics.physics.serialization.SHAPE_TYPE_RECTANGLE

class AddRectangleBodyCanvasFragment : AddBodyCanvasFragment() {
    override fun createWorldCanvas(): AddBodyWorldCanvas = AddRectangleObjectWorldCanvas(context, null)
    override val shapeType: String
        get() = SHAPE_TYPE_RECTANGLE
}
