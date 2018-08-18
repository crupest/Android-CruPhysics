package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.physics.serialization.CircleData
import crupest.cruphysics.physics.serialization.createShapeData
import crupest.cruphysics.preference.PreferenceAdapter
import crupest.cruphysics.preference.ShapeFloatPreferenceItem
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.mapPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by crupest on 2017/11/6.
 * View component [AddCircleBodyWorldCanvas].
 */
class AddCircleBodyWorldCanvas(context: Context?, attrs: AttributeSet?)
    : AddBodyWorldCanvas(context, attrs) {

    private val propertyPreferenceList = listOf(
            ShapeFloatPreferenceItem("CenterX:", { centerX }, {
                centerX = it
                updateControllerPosition()
                true
            }, signed = true),
            ShapeFloatPreferenceItem("CenterY:", { centerY }, {
                centerY = it
                updateControllerPosition()
                true
            }, signed = true),
            ShapeFloatPreferenceItem("Radius:", { radius }, {
                if (it <= 0.0f)
                    false
                else {
                    radius = it
                    updateControllerPosition()
                    true
                }
            }),
            ShapeFloatPreferenceItem("Angle:", { angle }, {
                angle = it
                updateControllerPosition()
                true
            }, signed = true)
    )

    override val controllers: Array<Controller> = arrayOf(
            Controller {
                centerX = it.x
                centerY = it.y
                propertyPreferenceList[0].setCurrentValue(centerX)
                propertyPreferenceList[1].setCurrentValue(centerY)
                updateControllerPosition()
                repaint()
            },
            Controller {
                radius = distance(centerX, centerY, it.x, it.y)
                angle = atan2(it.y - centerY, it.x - centerX)
                propertyPreferenceList[2].setCurrentValue(radius)
                propertyPreferenceList[3].setCurrentValue(angle)
                updateControllerPosition()
                repaint()
            }
    )

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var radius: Float = 300.0f
    private var angle: Float = 0.0f //in radian

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

    override fun createPropertyAdapter(): PreferenceAdapter =
            PreferenceAdapter(context, propertyPreferenceList)
}
