package crupest.cruphysics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.R
import crupest.cruphysics.component.AddRectangleBodyWorldCanvas
import crupest.cruphysics.viewmodel.AddRectangleBodyViewModel


class AddRectangleBodyCanvasFragment: AddBodyCanvasFragment() {

    private lateinit var viewModel: AddRectangleBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parent = parentFragment ?: throw IllegalStateException("Parent fragment is null.")

        viewModel = ViewModelProviders.of(parent).get(AddRectangleBodyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_rectangle_body_canvas, container, false)

        val canvas = rootView.findViewById<AddRectangleBodyWorldCanvas>(R.id.world_canvas)
        canvas.bindViewModel(canvasViewModel, this)
        canvas.bindViewModel(viewModel, this)

        return rootView
    }
}
