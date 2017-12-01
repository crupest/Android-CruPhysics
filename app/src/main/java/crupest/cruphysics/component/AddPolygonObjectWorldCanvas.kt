package crupest.cruphysics.component

import android.content.Context
import android.util.AttributeSet

/**
 * Created by crupest on 2017/12/1.
 * View component AddPolygonObjectWorldCanvas
 */

class AddPolygonObjectWorldCanvas(context: Context, attrs: AttributeSet)
    : AddObjectWorldCanvas(context, attrs) {

    override var controllers: Array<Controller> = Array(3) {
        Controller {
            onControllerMove()
        }
    }

    override fun reset() {
        //TODO!!!
    }

    private fun onControllerMove() {

    }
}
