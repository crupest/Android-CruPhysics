package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.MenuItem
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

    //TODO: Create view.

    //TODO: Save and recover shape data.
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
