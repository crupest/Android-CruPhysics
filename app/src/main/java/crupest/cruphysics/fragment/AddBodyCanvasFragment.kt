package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.*
import crupest.cruphysics.utility.showAlertDialog
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.MainViewModel

/**
 * Created by crupest on 2017/11/25.
 * Class [AddBodyCanvasFragment].
 */


abstract class AddBodyCanvasFragment : BaseFragment() {
    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var addBodyViewModel: AddBodyViewModel

    override fun determineOptionMenu(): IOptionMenuActivity.OptionMenuInfo? = staticOptionMenu(R.menu.next_menu, mapOf(
            R.id.next withHandler {
                val error = onValidate()
                if (error != null) {
                    showAlertDialog(context!!, error)
                } else {
                    val parent = parentFragment as NavigationFragment
                    parent.navigateTo(AddBodyPropertyFragment())
                }
            }
    ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        addBodyViewModel = ViewModelProviders.of(parent).get(AddBodyViewModel::class.java)
    }

    abstract fun onValidate(): String?
}
