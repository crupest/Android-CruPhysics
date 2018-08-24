package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import crupest.cruphysics.AddBodyActivity
import crupest.cruphysics.R
import crupest.cruphysics.SingleFragmentActivity
import crupest.cruphysics.component.AddBodyWorldCanvas

/**
 * Created by crupest on 2017/11/25.
 * Class [AddBodyCanvasFragment].
 */


abstract class AddBodyCanvasFragment : OptionMenuFragment(menuResource = R.menu.next_menu) {

    private lateinit var worldCanvas: AddBodyWorldCanvas

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_body_canvas, container, false)

        worldCanvas = createWorldCanvas()
        worldCanvas.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rootView.findViewById<FrameLayout>(R.id.world_canvas_layout).addView(worldCanvas, 0)

        val propertyList = rootView.findViewById<LinearLayout>(R.id.property_list)
        for (item in worldCanvas.propertyViewDelegates) {
            propertyList.addView(item.createView(inflater, propertyList))
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()

        val a = context as AddBodyActivity
        worldCanvas.setCamera(a.cameraData)
        worldCanvas.drawWorldDelegate = a.worldViewData

        a.shapeType = shapeType
        a.shapeInfoMap[shapeType]?.apply {
            worldCanvas.restoreShapeInfo(this)
        }
    }

    override fun onPause() {
        super.onPause()

        val a = context as AddBodyActivity
        a.cameraData = worldCanvas.generateCameraData()
        a.shapeInfoMap[shapeType] = worldCanvas.generateShapeInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val propertyList = view!!.findViewById<LinearLayout>(R.id.property_list)
        for ((index, value) in worldCanvas.propertyViewDelegates.withIndex()) {
            value.destroyView(propertyList.getChildAt(index))
        }
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.next) {
                val a = context as SingleFragmentActivity
                a.navigateToFragment(BodyPropertyFragment())
                true
            } else false

    protected abstract fun createWorldCanvas(): AddBodyWorldCanvas
    protected abstract val shapeType: String
}
