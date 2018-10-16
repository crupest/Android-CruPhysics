package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.MainViewModel

class AddBodyFragment : NavigationFragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(AddBodyViewModel::class.java)

        if (savedInstanceState == null)
            navigateToFragment(AddBodyShapeListFragment(), false)
    }

    fun createBodyAndPopBack() {

    }
}
