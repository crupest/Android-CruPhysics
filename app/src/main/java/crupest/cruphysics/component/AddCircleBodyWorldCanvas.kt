package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.serialization.data.ShapeInfo
import crupest.cruphysics.serialization.data.CircleData
import crupest.cruphysics.serialization.data.SHAPE_TYPE_CIRCLE
import crupest.cruphysics.serialization.data.createShapeData
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.mapPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KMutableProperty0

/**
 * Created by crupest on 2017/11/6.
 * View component [AddCircleBodyWorldCanvas].
 */
class AddCircleBodyWorldCanvas(context: Context?, attrs: AttributeSet?)
    : AddBodyWorldCanvas(context, attrs) {

    private fun createPropertyDelegate(property: KMutableProperty0<Double>,
                                       name: String? = null,
                                       biggerThan0: Boolean = false): ShapePropertyItemViewDelegate {
        fun getCalculatedName(): String {
            val pn = property.name
            if (pn.startsWith("world", ignoreCase = true))
                return pn.substring(5).capitalize() + ":"
            return pn.capitalize() + ":"
        }

        return ShapePropertyItemViewDelegate(
                name ?: getCalculatedName(),
                { property.get() },
                {
                    if (biggerThan0 && it <= 0.0) false else {
                        property.set(it)
                        updateControllerPosition()
                        repaint()
                        true
                    }
                },
                signed = !biggerThan0
        )
    }

    override val propertyViewDelegates = listOf(
            createPropertyDelegate(this::worldCenterX),
            createPropertyDelegate(this::worldCenterY),
            createPropertyDelegate(this::worldRadius, biggerThan0 = true),
            createPropertyDelegate(this::worldAngle)
    )

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                viewCenterX = it.x
                viewCenterY = it.y
                propertyViewDelegates[0].restoreText()
                propertyViewDelegates[1].restoreText()
                updateControllerPosition()
                repaint()
            },
            Controller {
                viewRadius = distance(viewCenterX, viewCenterY, it.x, it.y)
                viewAngle = atan2(it.y - viewCenterY, it.x - viewCenterX)
                propertyViewDelegates[2].restoreText()
                propertyViewDelegates[3].restoreText()
                updateControllerPosition()
                repaint()
            }
    )

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
        }

    private var worldRadius: Double
        get() = viewToWorld(viewRadius)
        set(value) {
            viewRadius = worldToView(value)
        }

    private var worldAngle: Double
        get() = -viewAngle.toDouble()
        set(value) {
            viewAngle = -value.toFloat()
        }

    private val centerController: Controller
        get() = controllers[0]

    private val radiusController: Controller
        get() = controllers[1]

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
        viewCenterX = width.toFloat() / 2.0f
        viewCenterY = height.toFloat() / 2.0f

        updateControllerPosition()
        repaint()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        val newCenter = matrix.mapPoint(viewCenterX, viewCenterY)
        viewCenterX = newCenter.x
        viewCenterY = newCenter.y

        viewRadius = matrix.mapRadius(viewRadius)

        updateControllerPosition()
        repaint()
    }

    override fun generateShapeInfo(): ShapeInfo {
        if (viewRadius == 0.0f)
            throw RuntimeException("Circle's radius must be bigger than 0.")

        return ShapeInfo(
                CircleData(radius = viewToWorld(viewRadius)).createShapeData(),
                viewToWorld(viewCenterX, viewCenterY),
                -viewAngle.toDouble() //because y-axis is reversed.
        )
    }

    override fun restoreShapeInfo(info: ShapeInfo) {
        require(info.shapeData.type == SHAPE_TYPE_CIRCLE)
        requireNotNull(info.shapeData.circleData)
        require(info.shapeData.circleData!!.radius != 0.0)

        worldCenterX = info.position.x
        worldCenterY = info.position.y
        worldRadius = info.shapeData.circleData!!.radius
        worldAngle= info.rotation
        updateControllerPosition()
    }

    override fun onSetCamera() {
        propertyViewDelegates.forEach { it.restoreText() }
    }
}
