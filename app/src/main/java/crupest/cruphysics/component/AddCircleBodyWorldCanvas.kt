package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.mapPoint
import crupest.cruphysics.viewmodel.AddCircleBodyViewModel
import crupest.cruphysics.viewmodel.setWhenNull
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by crupest on 2017/11/6.
 * View component [AddCircleBodyWorldCanvas].
 */
class AddCircleBodyWorldCanvas(context: Context?, attrs: AttributeSet?)
    : AddBodyWorldCanvas(context, attrs) {

    private var viewModel: AddCircleBodyViewModel? = null

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                viewCenterX = it.x
                viewCenterY = it.y
                viewModel?.centerX?.value = worldCenterX
                viewModel?.centerY?.value = worldCenterY
            },
            Controller {
                viewRadius = distance(viewCenterX, viewCenterY, it.x, it.y)
                viewAngle = atan2(it.y - viewCenterY, it.x - viewCenterX)
                viewModel?.radius?.value = worldRadius
                viewModel?.angle?.value = worldAngle
            }
    )

    private val centerController: Controller
        get() = controllers[0]

    private val radiusController: Controller
        get() = controllers[1]


    private var viewCenterX: Float = 0.0f
    private var viewCenterY: Float = 0.0f
    private var viewRadius: Float = 300.0f
    private var viewAngle: Float = 0.0f //in radian

    private var worldCenterX: Double
        get() = viewToWorld(viewCenterX, 0.0f).x
        set(value) {
            viewCenterX = worldToView(value, 0.0).x
        }

    private var worldCenterY: Double
        get() = viewToWorld(0.0f, viewCenterY).y
        set(value) {
            viewCenterY = worldToView(0.0, value).y
            updateControllerPosition()
        }

    private var worldRadius: Double
        get() = viewToWorld(viewRadius)
        set(value) {
            viewRadius = worldToView(value)
            updateControllerPosition()
        }

    private var worldAngle: Double
        get() = -viewAngle.toDouble()
        set(value) {
            viewAngle = -value.toFloat()
            updateControllerPosition()
        }

    private fun updateControllerPosition() {
        centerController.position.set(viewCenterX, viewCenterY)
        radiusController.position.set(
                viewCenterX + viewRadius * cos(viewAngle),
                viewCenterY + viewRadius * sin(viewAngle)
        )
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        //Don't need to rotate.
        canvas.drawCircle(
                viewCenterX,
                viewCenterY,
                viewRadius,
                bodyPaint,
                bodyBorderPaint
        )

        drawControllers(canvas)
    }

    override fun onInitialize() {
        viewModel.apply {
            checkNotNull(this) { "AddCircleBodyViewModel is not bound now." }
            if (centerX.value == null) {
                viewCenterX = width.toFloat() / 2.0f
                viewCenterY = height.toFloat() / 2.0f
                centerX.value = worldCenterX
                centerY.value = worldCenterY
                radius.value = worldRadius
                angle.value = worldAngle
            } else {
                worldCenterX = centerX.value!!
                worldCenterY = centerY.value!!
                worldRadius = radius.value!!
                worldAngle = angle.value!!
            }
        }

        updateControllerPosition()
        repaint()
    }

    override fun onViewMatrixPostConcat(matrixPostConcat: Matrix) {
        val newCenter = matrixPostConcat.mapPoint(viewCenterX, viewCenterY)
        viewCenterX = newCenter.x
        viewCenterY = newCenter.y

        viewRadius = matrixPostConcat.mapRadius(viewRadius)

        updateControllerPosition()
        repaint()
    }

    fun bindViewModel(viewModel: AddCircleBodyViewModel, lifecycleOwner: LifecycleOwner) {
        check(this.viewModel == null) { "AddCircleBodyViewModel is already bound." }

        this.viewModel = viewModel

        viewModel.centerX.observe(lifecycleOwner, Observer {
            worldCenterX = it
            updateControllerPosition()
            repaint()
        })
        viewModel.centerY.observe(lifecycleOwner, Observer {
            worldCenterY = it
            updateControllerPosition()
            repaint()
        })
        viewModel.radius.observe(lifecycleOwner, Observer {
            worldRadius = it
            updateControllerPosition()
            repaint()
        })
        viewModel.angle.observe(lifecycleOwner, Observer {
            worldAngle = it
            updateControllerPosition()
            repaint()
        })
    }
}
