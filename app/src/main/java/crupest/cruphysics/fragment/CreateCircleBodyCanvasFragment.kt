package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.R
import crupest.cruphysics.component.CreateCircleBodyWorldCanvas
import crupest.cruphysics.viewmodel.bindDoubleLiveData
import crupest.cruphysics.viewmodel.CreateCircleBodyViewModel

class CreateCircleBodyCanvasFragment : CreateBodyCanvasFragment() {

    private lateinit var viewModel: CreateCircleBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")

        viewModel = ViewModelProviders.of(parent).get(CreateCircleBodyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_create_circle_body_canvas, container, false)

        val canvas = rootView.findViewById<CreateCircleBodyWorldCanvas>(R.id.world_canvas)
        canvas.bindViewModel(mainViewModel, this.viewLifecycleOwner)
        canvas.bindColorLiveData(propertyViewModel.bodyColor, this.viewLifecycleOwner)
        canvas.bindViewModel(viewModel, this.viewLifecycleOwner)

        fun bindEditText(id: Int, liveData: MutableLiveData<Double>, noLessThan0: Boolean = false) {
            rootView.findViewById<EditText>(id).bindDoubleLiveData(this.viewLifecycleOwner, liveData, noLessThan0 = noLessThan0)
        }

        bindEditText(R.id.center_x_value, viewModel.centerX)
        bindEditText(R.id.center_y_value, viewModel.centerY)
        bindEditText(R.id.radius_value, viewModel.radius, true)
        bindEditText(R.id.angle_value, viewModel.angle)

        return rootView
    }

    override fun validate(): String? {
        if (viewModel.radius.value == 0.0)
            return "Radius can't be 0."
        return null
    }
}
