package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.serialization.data.SHAPE_TYPE_CIRCLE
import crupest.cruphysics.serialization.data.SHAPE_TYPE_RECTANGLE
import crupest.cruphysics.serialization.deserializeAsMassType
import crupest.cruphysics.viewmodel.AddBodyViewModel
import crupest.cruphysics.viewmodel.AddCircleBodyViewModel
import crupest.cruphysics.viewmodel.AddRectangleBodyViewModel
import crupest.cruphysics.viewmodel.MainViewModel
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Vector2

class AddBodyFragment : NavigationFragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: AddBodyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(AddBodyViewModel::class.java)
    }

    override fun onNavigateToFirstFragment(): BaseFragment = AddBodyShapeListFragment()

    fun createBodyAndPopBack() {
        val position = Vector2()
        val angle: Double
        val shape: Convex

        when (viewModel.shapeType.value) {
            SHAPE_TYPE_CIRCLE -> {
                val shapeViewModel = ViewModelProviders.of(this).get(AddCircleBodyViewModel::class.java)
                position.x = shapeViewModel.centerX.value!!
                position.y = shapeViewModel.centerY.value!!
                angle = shapeViewModel.angle.value!!
                shape = Circle(shapeViewModel.radius.value!!)
            }
            SHAPE_TYPE_RECTANGLE -> {
                val shapeViewModel = ViewModelProviders.of(this).get(AddRectangleBodyViewModel::class.java)
                position.x = shapeViewModel.centerX.value!!
                position.y = shapeViewModel.centerY.value!!
                angle = shapeViewModel.angle.value!!
                shape = Rectangle(shapeViewModel.width.value!!, shapeViewModel.height.value!!)
            }
            else -> throw IllegalStateException("Unknown shape type.")
        }

        val body = Body()
        body.addFixture(shape, viewModel.density.value!!, viewModel.friction.value!!, viewModel.restitution.value!!)
        body.translate(position)
        body.rotateAboutCenter(angle)

        body.setMass(viewModel.bodyType.value!!.deserializeAsMassType())

        body.linearVelocity = Vector2(viewModel.velocityX.value!!, viewModel.velocityY.value!!)
        body.angularVelocity = viewModel.angularVelocity.value!!

        body.userData = BodyUserData(body, viewModel.bodyColor.value!!)

        mainViewModel.addBody(body)

        getParentNavigator().popBackStack()
    }
}
