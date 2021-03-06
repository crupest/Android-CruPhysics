package crupest.cruphysics.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import crupest.cruphysics.IOptionMenuActivity
import crupest.cruphysics.Observable
import crupest.cruphysics.R
import crupest.cruphysics.dynamicOptionMenu
import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.physics.ShapeType
import crupest.cruphysics.utility.showAlertDialog
import crupest.cruphysics.viewmodel.*
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Vector2

class CreateBodyFragment : NavigationFragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: CreateBodyViewModel
    private lateinit var propertyViewModel: BodyPropertyViewModel

    private val optionMenuRes: Observable<Int> = Observable(R.menu.next_menu)

    override fun determineOptionMenu(): IOptionMenuActivity.OptionMenuInfo? = dynamicOptionMenu(optionMenuRes) {
        addHandler(R.id.next) {
            val fragment = getCurrentFragment()
            if (fragment is CreateBodyShapeListFragment) {
                val type = fragment.getCurrentShapeType()
                viewModel.shapeType.value = type
                when (type) {
                    ShapeType.CIRCLE -> navigateTo(CreateCircleBodyCanvasFragment())
                    ShapeType.RECTANGLE -> navigateTo(CreateRectangleBodyCanvasFragment())
                }
                return@addHandler
            }

            if (fragment is CreateBodyCanvasFragment) {
                val error = fragment.validate()
                if (error != null) {
                    showAlertDialog(context!!, error)
                } else {
                    navigateTo(CreateBodyPropertyFragment())
                }
                return@addHandler
            }

            throw IllegalStateException("You can't reach here!!!")
        }

        addHandler(R.id.ok) {
            val fragment = getCurrentFragment() as BaseBodyPropertyFragment
            val error = fragment.validate()
            if (error != null)
                showAlertDialog(context!!, error)
            else
                createBodyAndPopBack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = context as FragmentActivity
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(CreateBodyViewModel::class.java)
        propertyViewModel = ViewModelProviders.of(this).get(BodyPropertyViewModel::class.java)

        if (savedInstanceState == null)
            propertyViewModel.initDefault()
    }

    override fun onNavigateToFirstFragment(): BaseFragment = CreateBodyShapeListFragment()

    override fun onNavigate(newFragment: BaseFragment) {
        optionMenuRes.value = when (newFragment) {
            is CreateBodyShapeListFragment, is CreateBodyCanvasFragment -> R.menu.next_menu
            is BaseBodyPropertyFragment -> R.menu.check_menu
            else -> throw IllegalStateException("You can't reach here.")
        }
    }

    private fun createBodyAndPopBack() {
        val position = Vector2()
        val angle: Double
        val shape: Convex

        when (viewModel.shapeType.value!!) {
            ShapeType.CIRCLE -> {
                val shapeViewModel = ViewModelProviders.of(this).get(CreateCircleBodyViewModel::class.java)
                position.x = shapeViewModel.centerX.value!!
                position.y = shapeViewModel.centerY.value!!
                angle = shapeViewModel.angle.value!!
                shape = Circle(shapeViewModel.radius.value!!)
            }
            ShapeType.RECTANGLE -> {
                val shapeViewModel = ViewModelProviders.of(this).get(CreateRectangleBodyViewModel::class.java)
                position.x = shapeViewModel.centerX.value!!
                position.y = shapeViewModel.centerY.value!!
                angle = shapeViewModel.angle.value!!
                shape = Rectangle(shapeViewModel.width.value!!, shapeViewModel.height.value!!)
            }
        }

        mainViewModel.addBody(propertyViewModel.createBody(shape, position, angle))

        getParentNavigator().popBackStack()
    }
}
