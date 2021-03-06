package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.toDegrees
import crupest.cruphysics.viewmodel.CreateRectangleBodyViewModel
import kotlin.math.*

/**
 * Created by crupest on 2017/11/17.
 * View component [CreateRectangleBodyWorldCanvas].
 */
class CreateRectangleBodyWorldCanvas(context: Context?, attrs: AttributeSet?)
    : CreateBodyWorldCanvas(context, attrs) {

    private var viewModel: CreateRectangleBodyViewModel? = null

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                viewCenterX = it.x
                viewCenterY = it.y
                viewModel?.centerX?.value = worldCenterX
                viewModel?.centerY?.value = worldCenterY
            },
            Controller {
                val halfDiagonal = distance(viewCenterX, viewCenterY, it.x, it.y)
                val a = atan2(it.y - viewCenterY, it.x - viewCenterX) - viewAngle
                viewHalfWidth = halfDiagonal * cos(a).coerceAtLeast(0.0f)
                viewHalfHeight = halfDiagonal * sin(a).coerceAtLeast(0.0f)
                viewModel?.width?.value = worldWidth
                viewModel?.height?.value = worldHeight
            },
            Controller {
                viewAngle = atan2(it.y - viewCenterY, it.x - viewCenterX)
                viewModel?.angle?.value = worldAngle
            }
    )

    private var viewCenterX: Float = 0.0f
    private var viewCenterY: Float = 0.0f
    private var viewHalfWidth: Float = 200.0f
    private var viewHalfHeight: Float = 100.0f
    private var viewAngle: Float = 0.0f // in radian

    private var worldCenterX: Double
        get() = viewToWorld(viewCenterX, 0.0f).x
        set(value) {
            viewCenterX = worldToView(value, 0.0).x
        }

    private var worldCenterY: Double
        get() = viewToWorld(0.0f, viewCenterY).y
        set(value) {
            viewCenterY = worldToView(0.0, value).y
        }

    private var worldWidth: Double
        get() = viewToWorld(viewHalfWidth) * 2
        set(value) {
            viewHalfWidth = worldToView(value / 2.0f)
        }

    private var worldHeight: Double
        get() = viewToWorld(viewHalfHeight) * 2
        set(value) {
            viewHalfHeight = worldToView(value / 2.0f)
        }

    private var worldAngle: Double
        get() = -viewAngle.toDouble()
        set(value) {
            viewAngle = -value.toFloat()
        }

    private val positionController
        get() = controllers[0]

    private val sizeController
        get() = controllers[1]

    private val rotationController
        get() = controllers[2]

    private fun updateControllerPosition() {
        positionController.position.set(viewCenterX, viewCenterY)

        val a = viewAngle + atan2(viewHalfHeight, viewHalfWidth)
        val halfDiagonal = sqrt((viewHalfWidth).pow(2) + (viewHalfHeight).pow(2))

        sizeController.position.set(viewCenterX + halfDiagonal * cos(a), viewCenterY + halfDiagonal * sin(a))

        val l = viewHalfWidth + 80
        rotationController.position.set(viewCenterX + l * cos(viewAngle), viewCenterY + l * sin(viewAngle))
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        canvas.save()
        canvas.rotate(viewAngle.toDegrees(), viewCenterX, viewCenterY)
        canvas.drawRectangle(
                viewCenterX - viewHalfWidth,
                viewCenterY - viewHalfHeight,
                viewCenterX + viewHalfWidth,
                viewCenterY + viewHalfHeight,
                bodyPaint,
                bodyBorderPaint
        )
        canvas.restore()

        drawControllers(canvas)
    }

    override fun onInitialize() {
        viewModel.apply {
            checkNotNull(this) { "CreateRectangleBodyViewModel is not bound now." }
            if (centerX.value == null) {
                viewCenterX = this@CreateRectangleBodyWorldCanvas.width.toFloat() / 2.0f
                viewCenterY = this@CreateRectangleBodyWorldCanvas.height.toFloat() / 2.0f
                centerX.value = worldCenterX
                centerY.value = worldCenterY
                width.value = worldWidth
                height.value = worldHeight
                angle.value = worldAngle
            } else {
                worldCenterX = centerX.value!!
                worldCenterY = centerY.value!!
                worldWidth = width.value!!
                worldHeight = height.value!!
                worldAngle = angle.value!!
            }
        }

        updateControllerPosition()
        repaint()
    }

    override fun onCameraChanged(newMatrix: Matrix) {
        viewModel!!.apply {
            worldCenterX = centerX.value!!
            worldCenterY = centerY.value!!
            worldWidth = width.value!!
            worldHeight = height.value!!
            worldAngle = angle.value!!
        }
    }

    fun bindViewModel(viewModel: CreateRectangleBodyViewModel, lifecycleOwner: LifecycleOwner) {
        check(this.viewModel == null) { "CreateRectangleBodyViewModel is already bound." }

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
        viewModel.width.observe(lifecycleOwner, Observer {
            worldWidth = it
            updateControllerPosition()
            repaint()
        })
        viewModel.height.observe(lifecycleOwner, Observer {
            worldHeight = it
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
