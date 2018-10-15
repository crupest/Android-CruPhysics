package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.R
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.MainViewModel

/**
 * Created by crupest on 2017/11/25.
 * Class [AddBodyCanvasFragment].
 */


abstract class AddBodyCanvasFragment : OptionMenuFragment() {

    init {
        optionMenuRes = R.menu.next_menu
    }

    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var canvasViewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        canvasViewModel = ViewModelProviders.of(parent).get(AddBodyViewModel::class.java)
    }

    override fun onOptionMenuItemSelected(menuItem: MenuItem): Boolean =
            if (menuItem.itemId == R.id.next) {
                val parent = parentFragment as NavigationFragment
                parent.navigateToFragment(BodyPropertyFragment())
                true
            } else false
}
