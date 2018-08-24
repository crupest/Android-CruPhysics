package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.serialization.data.ShapeInfo
import crupest.cruphysics.serialization.data.RectangleData
import crupest.cruphysics.serialization.data.SHAPE_TYPE_RECTANGLE
import crupest.cruphysics.serialization.data.createShapeData
import crupest.cruphysics.utility.distance
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.mapPoint
import crupest.cruphysics.utility.toDegrees
import kotlin.math.*
import kotlin.reflect.KMutableProperty0

/**
 * Created by crupest on 2017/11/17.
 * View component AddRectangleObjectWorldCanvas
 */
class AddRectangleObjectWorldCanvas(context: Context?, attrs: AttributeSet?)
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
            createPropertyDelegate(this::worldWidth, biggerThan0 = true),
            createPropertyDelegate(this::worldHeight, biggerThan0 = true),
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

                val halfDiagonal = distance(centerX, centerY, it.x, it.y)
                val a = atan2(it.y - centerY, it.x - centerX) - angle
                halfWidth = halfDiagonal * cos(a).coerceAtLeast(0.0f)
                halfHeight = halfDiagonal * sin(a).coerceAtLeast(0.0f)
                propertyViewDelegates[2].restoreText()
                propertyViewDelegates[3].restoreText()
                updateControllerPosition()
                repaint()
            },
            Controller {
                angle = atan2(it.y - centerY, it.x - centerX)
                propertyViewDelegates[4].restoreText()
                updateControllerPosition()
                repaint()
            }
    )

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var halfWidth: Float = 200.0f
    private var halfHeight: Float = 100.0f
    private var angle: Float = 0.0f // in radian

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

    private var worldWidth: Double
    get() = viewToWorld(halfWidth) * 2
    set(value) {
        halfWidth = worldToView(value / 2.0f)
    }

    private var worldHeight: Double
    get() = viewToWorld(halfHeight) * 2
    set(value) {
        halfHeight = worldToView(value / 2.0f)
    }

    private var worldAngle: Double
        get() = angle.toDouble()
        set(value) {
            angle = value.toFloat()
        }

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
                        width = worldWidth,
                        height = worldHeight
                ).createShapeData(),
                viewToWorld(centerX, centerY),
                -angle.toDouble() //because y-axis is reversed.
        )
    }

    override fun restoreShapeInfo(info: ShapeInfo) {
        require(info.shapeData.type == SHAPE_TYPE_RECTANGLE)
        requireNotNull(info.shapeData.rectangleData)
        require(info.shapeData.rectangleData!!.width != 0.0)
        require(info.shapeData.rectangleData!!.height != 0.0)

        centerX = worldToView(info.position.x)
        centerY = worldToView(info.position.y)
        halfWidth = worldToView(info.shapeData.rectangleData!!.width / 2)
        halfHeight = worldToView(info.shapeData.rectangleData!!.height /2)
        angle = -info.rotation.toFloat()
        updateControllerPosition()
    }

    override fun onSetCamera() {
        propertyViewDelegates.forEach { it.restoreText() }
    }
}
