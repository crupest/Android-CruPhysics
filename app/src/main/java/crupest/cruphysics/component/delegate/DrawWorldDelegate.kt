package crupest.cruphysics.component.delegate

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.LiveData
import crupest.cruphysics.physics.*
import crupest.cruphysics.physics.view.BodyViewData
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.utility.drawCircle
import crupest.cruphysics.utility.drawRectangle
import crupest.cruphysics.utility.strokePaint
import org.dyn4j.dynamics.Body

class DrawWorldDelegate(private val camera: LiveData<CameraData>) : IDrawDelegate {
    companion object {
        private const val BORDER_WIDTH = 8.0f
    }

    private val bodyViewDataMap: MutableMap<Body, BodyViewData> = mutableMapOf()

    private val bodyBorderPaint = strokePaint(Color.BLACK)

    private val cameraObserver = { it: CameraData ->
        bodyBorderPaint.strokeWidth = BORDER_WIDTH / it.scale.toFloat()
    }

    init {
        camera.value?.also(cameraObserver)
        camera.observeForever(cameraObserver)
    }

    fun registerBody(body: Body) {
        if (body in bodyViewDataMap)
            throw IllegalArgumentException("The body has already been registered.")
        bodyViewDataMap[body] = BodyViewData(body.cruUserData.color)
    }

    fun unregisterBody(body: Body) {
        if (body !in bodyViewDataMap)
            throw IllegalArgumentException("The body hasn't been registered.")
        bodyViewDataMap.remove(body)
    }

    fun unregisterAllBody() {
        bodyViewDataMap.clear()
    }

    fun updateBody(body: Body) {
        val viewData = bodyViewDataMap[body] ?: throw IllegalArgumentException("The body hasn't been registered.")
        if (body.cruUserData.color != viewData.color)
            viewData.color = body.cruUserData.color
    }

    fun registerBodies(bodies: Iterable<Body>) {
        bodies.forEach {
            registerBody(it)
        }
    }

    fun clearAndRegister(bodies: Iterable<Body>) {
        unregisterAllBody()
        registerBodies(bodies)
    }

    operator fun get(body: Body): BodyViewData {
        return bodyViewDataMap[body]
                ?: throw IllegalArgumentException("The body hasn't been registered.")
    }

    override fun draw(canvas: Canvas) {
        for ((body, bodyViewData) in bodyViewDataMap) {
            val shape = body.checkAndGetFixture().shape
            canvas.withTransform(body.transform) {
                shape.switchShape {
                    onCircle {
                        drawCircle(
                                it.center.x.toFloat(),
                                it.center.y.toFloat(),
                                it.radius.toFloat(),
                                fill = bodyViewData.paint,
                                stroke = bodyBorderPaint
                        )
                    }
                    onRectangle {
                        val hw = it.width / 2.0
                        val hh = it.height / 2.0
                        drawRectangle(
                                (it.center.x - hw).toFloat(),
                                (it.center.y + hh).toFloat(),
                                (it.center.x + hw).toFloat(),
                                (it.center.y - hh).toFloat(),
                                fill = bodyViewData.paint,
                                stroke = bodyBorderPaint
                        )
                    }
                }
            }
        }
    }

    fun generateThumbnail(width: Int, height: Int, camera: CameraData): Bitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
                concat(camera.fromData(
                        width.toFloat() / 2.0f, height.toFloat() / 2.0f))
                draw(this)
            }

    fun onClear() {
        unregisterAllBody()
        camera.removeObserver(cameraObserver)
    }
}
