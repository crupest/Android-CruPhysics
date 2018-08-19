package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
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

    protected abstract fun createWorldCanvas(): AddBodyWorldCanvas

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_body_canvas, container, false)

        worldCanvas = createWorldCanvas()
        worldCanvas.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rootView.findViewById<FrameLayout>(R.id.world_canvas_layout).addView(worldCanvas)

        val typeSpinner: Spinner = rootView.findViewById(R.id.body_type_spinner)
        val adapter = ArrayAdapter(context, R.layout.object_type_spinner_item, R.id.content,
                context!!.resources.getStringArray(R.array.object_type_list))
        typeSpinner.adapter = adapter
        typeSpinner.setSelection(0)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = context as AddBodyActivity
        worldCanvas.setCamera(activity.cameraData)
        worldCanvas.drawWorldDelegate = activity.worldViewData
    }


    override fun onPause() {
        super.onPause()
        (activity as AddBodyActivity).cameraData = worldCanvas.generateCameraData()
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.next) {
                val a = context as SingleFragmentActivity
                a.navigateToFragment(BodyPropertyFragment())
                true
            } else false
}
