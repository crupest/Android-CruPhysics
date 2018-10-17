package crupest.cruphysics.component.delegate

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.applyCanvas
import crupest.cruphysics.physics.*
import crupest.cruphysics.physics.view.BodyViewData
import crupest.cruphysics.serialization.data.CameraData
import org.dyn4j.dynamics.Body

class WorldCanvasDelegate(): IDrawDelegate {
    constructor(bodies: List<Body>) : this() {
        bodies.forEach {
            registerBody(it)
        }
    }

    private val bodyViewDataMap: MutableMap<Body, BodyViewData> = mutableMapOf()

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

    operator fun get(body: Body): BodyViewData {
        return bodyViewDataMap[body]
                ?: throw IllegalArgumentException("The body hasn't been registered.")
    }

    override fun draw(canvas: Canvas) {
        for ((body, bodyViewData) in bodyViewDataMap) {
            val shape = body.checkAndGetFixture().shape
            canvas.withTransform(body.transform) {
                shape.switchShape(
                        circleHandler = {
                            drawCircle(
                                    it.center.x.toFloat(),
                                    it.center.y.toFloat(),
                                    it.radius.toFloat(),
                                    bodyViewData.paint
                            )
                        },
                        rectangleHandler = {
                            val hw = it.width / 2.0
                            val hh = it.height / 2.0
                            drawRect(
                                    (it.center.x - hw).toFloat(),
                                    (it.center.y + hh).toFloat(),
                                    (it.center.x + hw).toFloat(),
                                    (it.center.y - hh).toFloat(),
                                    bodyViewData.paint
                            )
                        }
                )
            }
        }
    }

    fun generateThumbnail(width: Int, height: Int, camera: CameraData): Bitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
                drawColor(Color.WHITE)
                concat(camera.fromData(
                        width.toFloat() / 2.0f, height.toFloat() / 2.0f))
                draw(this)
            }
}
