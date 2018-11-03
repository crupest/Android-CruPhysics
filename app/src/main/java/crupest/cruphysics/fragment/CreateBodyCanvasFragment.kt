package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.viewmodel.BodyPropertyViewModel
import crupest.cruphysics.viewmodel.MainViewModel

/**
 * Created by crupest on 2017/11/25.
 * Class [CreateBodyCanvasFragment].
 */


abstract class CreateBodyCanvasFragment : BaseFragment() {
    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var propertyViewModel: BodyPropertyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")
        propertyViewModel = ViewModelProviders.of(parent).get(BodyPropertyViewModel::class.java)
    }

    abstract fun validate(): String?
}
