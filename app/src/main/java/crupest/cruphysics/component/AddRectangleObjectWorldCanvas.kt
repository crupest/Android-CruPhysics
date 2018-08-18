package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.physics.serialization.RectangleData
import crupest.cruphysics.physics.serialization.createShapeData
import crupest.cruphysics.preference.PreferenceAdapter
import crupest.cruphysics.preference.ShapeFloatPreferenceItem
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.mapPoint
import crupest.cruphysics.utility.toDegrees
import kotlin.math.*

/**
 * Created by crupest on 2017/11/17.
 * View component AddRectangleObjectWorldCanvas
 */
class AddRectangleObjectWorldCanvas(context: Context?, attrs: AttributeSet?)
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
            ShapeFloatPreferenceItem("Width:", { halfWidth * 2 }, {
                if (it <= 0.0f)
                    false
                else {
                    halfWidth = it / 2.0f
                    updateControllerPosition()
                    true
                }
            }),
            ShapeFloatPreferenceItem("Height:", { halfHeight * 2 }, {
                if (it <= 0.0f)
                    false
                else {
                    halfHeight = it / 2.0f
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

                val halfDiagonal = distance(centerX, centerY, it.x, it.y)
                val a = atan2(it.y - centerY, it.x - centerX) - angle
                halfWidth = halfDiagonal * cos(a).coerceAtLeast(0.0f)
                halfHeight = halfDiagonal * sin(a).coerceAtLeast(0.0f)
                propertyPreferenceList[2].setCurrentValue(halfWidth * 2)
                propertyPreferenceList[3].setCurrentValue(halfHeight * 2)
                updateControllerPosition()
                repaint()
            },
            Controller {
                angle = atan2(it.y - centerY, it.x - centerX)
                propertyPreferenceList[4].setCurrentValue(angle)
                updateControllerPosition()
                repaint()
            }
    )

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var halfWidth: Float = 200.0f
    private var halfHeight: Float = 100.0f
    private var angle: Float = 0.0f // in radian

    private val positionController
        get() = controllers[0]

    private val sizeController
        get() = controllers[1]

    private val rotationController
        get() = controllers[2]

    private fun updateControllerPosition() {
        positionController.position.set(centerX, centerY)

        val a = angle + atan2(halfHeight, halfWidth)
        val halfDiagonal = sqrt((halfWidth).pow(2) + (halfHeight).pow(2))

        sizeController.position.set(centerX + halfDiagonal * cos(a), centerY + halfDiagonal * sin(a))

        val l = halfWidth + 80
        rotationController.position.set(centerX + l * cos(angle), centerY + l * sin(angle))
    }

    override fun onPaint(canvas: Canvas) {
        super.onPaint(canvas)

        canvas.save()
        canvas.rotate(angle.toDegrees(), centerX, centerY)
        canvas.drawRectangle(
                centerX - halfWidth,
                centerY - halfHeight,
                centerX + halfWidth,
                centerY + halfHeight,
                bodyPaint,
                bodyBorderPaint
        )
        canvas.restore()

        drawControllers(canvas)
    }

    override fun onInitialize() {
        centerX = width.toFloat() / 2.0f
        centerY = height.toFloat() / 2.0f

        updateControllerPosition()
        repaint()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        matrix.mapPoint(centerX, centerY).let {
            centerX = it.x
            centerY = it.y
        }
        halfWidth = matrix.mapRadius(halfWidth)
        halfHeight = matrix.mapRadius(halfHeight)

        updateControllerPosition()
        repaint()
    }

    override fun generateShapeInfo(): ShapeInfo {
        if (halfWidth == 0.0f)
            throw RuntimeException("Rectangle's width must be bigger than 0.")
        if (halfHeight == 0.0f)
            throw RuntimeException("Rectangle's height must be bigger than 0.")

        return ShapeInfo(
                RectangleData(
                        width = viewToWorld(halfWidth * 2.0f),
                        height = viewToWorld(halfHeight * 2.0f)
                ).createShapeData(),
                viewToWorld(centerX, centerY),
                -angle.toDouble() //because y-axis is reversed.
        )
    }

    override fun createPropertyAdapter(): PreferenceAdapter = PreferenceAdapter(
            context, propertyPreferenceList)
}
