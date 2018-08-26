package crupest.cruphysics.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import crupest.cruphysics.serialization.data.*
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
                viewCenterX = it.x
                viewCenterY = it.y
                propertyViewDelegates[0].restoreText()
                propertyViewDelegates[1].restoreText()
                updateControllerPosition()
                repaint()
            },
            Controller {

                val halfDiagonal = distance(viewCenterX, viewCenterY, it.x, it.y)
                val a = atan2(it.y - viewCenterY, it.x - viewCenterX) - viewAngle
                viewHalfWidth = halfDiagonal * cos(a).coerceAtLeast(0.0f)
                viewHalfHeight = halfDiagonal * sin(a).coerceAtLeast(0.0f)
                propertyViewDelegates[2].restoreText()
                propertyViewDelegates[3].restoreText()
                updateControllerPosition()
                repaint()
            },
            Controller {
                viewAngle = atan2(it.y - viewCenterY, it.x - viewCenterX)
                propertyViewDelegates[4].restoreText()
                updateControllerPosition()
                repaint()
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
        viewCenterX = width.toFloat() / 2.0f
        viewCenterY = height.toFloat() / 2.0f

        updateControllerPosition()
        repaint()
    }

    override fun onViewMatrixChanged(matrix: Matrix) {
        matrix.mapPoint(viewCenterX, viewCenterY).let {
            viewCenterX = it.x
            viewCenterY = it.y
        }
        viewHalfWidth = matrix.mapRadius(viewHalfWidth)
        viewHalfHeight = matrix.mapRadius(viewHalfHeight)

        updateControllerPosition()
        repaint()
    }

    override fun generateShapeInfo(): ShapeInfo {
        if (viewHalfWidth == 0.0f)
            throw RuntimeException("Rectangle's width must be bigger than 0.")
        if (viewHalfHeight == 0.0f)
            throw RuntimeException("Rectangle's height must be bigger than 0.")

        return ShapeInfo(
                RectangleData(
                        width = worldWidth,
                        height = worldHeight
                ).createShapeData(),
                Vector2Data(worldCenterX, worldCenterY),
                worldAngle
        )
    }

    override fun restoreShapeInfo(info: ShapeInfo) {
        require(info.shapeData.type == SHAPE_TYPE_RECTANGLE)
        requireNotNull(info.shapeData.rectangleData)
        require(info.shapeData.rectangleData!!.width != 0.0)
        require(info.shapeData.rectangleData!!.height != 0.0)

        worldCenterX = info.position.x
        worldCenterY = info.position.y
        worldWidth = info.shapeData.rectangleData!!.width
        worldHeight = info.shapeData.rectangleData!!.height
        worldAngle = info.rotation
        updateControllerPosition()
    }

    override fun onSetCamera() {
        propertyViewDelegates.forEach { it.restoreText() }
    }
}
