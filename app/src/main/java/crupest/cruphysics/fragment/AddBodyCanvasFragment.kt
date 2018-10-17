package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.Observable
import crupest.cruphysics.R
import crupest.cruphysics.utility.showAlertDialog
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.MainViewModel

/**
 * Created by crupest on 2017/11/25.
 * Class [AddBodyCanvasFragment].
 */


abstract class AddBodyCanvasFragment : Fragment() {
    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var addBodyViewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as IOptionMenuActivity).setOptionMenu(this, Observable(R.menu.next_menu)) {
            if (it.itemId == R.id.next) {
                val error = onValidate()
                if (error != null) {
                    showAlertDialog(context!!, error)
                } else {
                    val parent = parentFragment as NavigationFragment
                    parent.navigateToFragment(AddBodyPropertyFragment())
                }
                true
            } else false
        }

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        addBodyViewModel = ViewModelProviders.of(parent).get(AddBodyViewModel::class.java)
    }

    abstract fun onValidate(): String?
}
