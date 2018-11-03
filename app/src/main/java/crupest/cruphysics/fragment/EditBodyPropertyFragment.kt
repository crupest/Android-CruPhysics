package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.R
import crupest.cruphysics.staticOptionMenu
import crupest.cruphysics.utility.showAlertDialog
import crupest.cruphysics.viewmodel.BodyPropertyViewModel
import crupest.cruphysics.viewmodel.MainViewModel
import org.dyn4j.dynamics.Body
import java.util.*

class EditBodyPropertyFragment : BaseBodyPropertyFragment() {
    companion object {
        private const val ARG_BODY_ID = "body-id"

        fun newInstance(bodyId: UUID) =
                EditBodyPropertyFragment().apply { arguments = Bundle().apply { putString(ARG_BODY_ID, bodyId.toString()) } }
    }

    private lateinit var body: Body

    private lateinit var mainViewModel: MainViewModel

    override fun onSetViewModel(): BodyPropertyViewModel {
        return ViewModelProviders.of(this).get(BodyPropertyViewModel::class.java)
    }

    override fun determineOptionMenu(): IOptionMenuActivity.OptionMenuInfo? = staticOptionMenu(R.menu.check_menu) {
        addHandler(R.id.ok) {
            val error = validate()
            if (error != null)
                showAlertDialog(context!!, error)
            else {
                viewModel.writeToBody(body)
                mainViewModel.updateBody(body)
                getParentNavigator().popBackStack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        body = mainViewModel.getBodyById(UUID.fromString(arguments!!.getString(ARG_BODY_ID)!!))

        if (savedInstanceState == null) {
            viewModel.fromBody(body)
        }
    }
}
