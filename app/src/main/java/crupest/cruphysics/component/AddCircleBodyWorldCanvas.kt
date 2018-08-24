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
                centerX = it.x
                centerY = it.y
                propertyViewDelegates[0].restoreText()
                propertyViewDelegates[1].restoreText()
                updateControllerPosition()
                repaint()
            },
            Controller {
                radius = distance(centerX, centerY, it.x, it.y)
                angle = atan2(it.y - centerY, it.x - centerX)
                propertyViewDelegates[2].restoreText()
                propertyViewDelegates[3].restoreText()
                updateControllerPosition()
                repaint()
            }
    )

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var radius: Float = 300.0f
    private var angle: Float = 0.0f //in radian

    private var worldCenterX: Double
        get() = viewToWorld(centerX, 0.0f).x
        set(value) {
            centerX = worldToView(value, 0.0).x
        }

    private var worldCenterY: Double
        get() = viewToWorld(0.0f, centerY).y
        set(value) {
            centerY = worldToView(0.0, value).y
        }

    private var worldRadius: Double
        get() = viewToWorld(radius)
        set(value) {
            radius = worldToView(value)
        }

    private var worldAngle: Double
        get() = angle.toDouble()
        set(value) {
            angle = value.toFloat()
        }

    private val centerController: Controller
        get() = controllers[0]

    private val radiusController: Controller
        get() = controllers[1]

    private fun updateControllerPosition() {
        centerController.position.set(centerX, centerY)
        radiusController.position.set(
                centerX + radius * cos(angle),
                centerY + radius * sin(angle)
        )
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        //Don't need to rotate.
        canvas.drawCircle(
                centerX,
                centerY,
                radius,
                bodyPaint,
                bodyBorderPaint
        )

        drawControllers(canvas)
    }

    override fun onInitialize() {
        centerX = width.toFloat() / 2.0f
        centerY = height.toFloat() / 2.0f

        updateControllerPosition()
        repaint()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        val newCenter = matrix.mapPoint(centerX, centerY)
        centerX = newCenter.x
        centerY = newCenter.y

        radius = matrix.mapRadius(radius)

        updateControllerPosition()
        repaint()
    }

    override fun generateShapeInfo(): ShapeInfo {
        if (radius == 0.0f)
            throw RuntimeException("Circle's radius must be bigger than 0.")

        return ShapeInfo(
                CircleData(radius = viewToWorld(radius)).createShapeData(),
                viewToWorld(centerX, centerY),
                -angle.toDouble() //because y-axis is reversed.
        )
    }

    override fun restoreShapeInfo(info: ShapeInfo) {
        require(info.shapeData.type == SHAPE_TYPE_CIRCLE)
        requireNotNull(info.shapeData.circleData)
        require(info.shapeData.circleData!!.radius != 0.0)

        centerX = worldToView(info.position.x)
        centerY = worldToView(info.position.y)
        radius = worldToView(info.shapeData.circleData!!.radius)
        angle = -info.rotation.toFloat()
        updateControllerPosition()
    }

    override fun onSetCamera() {
        propertyViewDelegates.forEach { it.restoreText() }
    }
}
