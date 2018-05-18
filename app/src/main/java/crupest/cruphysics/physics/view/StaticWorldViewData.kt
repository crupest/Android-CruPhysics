package crupest.cruphysics.physics.view

import android.graphics.Canvas
import crupest.cruphysics.component.IDrawWorldDelegate
import crupest.cruphysics.physics.serialization.BodyData
import crupest.cruphysics.physics.serialization.WorldData

class StaticWorldViewData(val worldData: WorldData) : IDrawWorldDelegate {
    private val bodyViewDataMap: Map<BodyData, BodyViewData> = worldData.bodies.associate {
        it to BodyViewData(color = it.appearance.color)
    }

    override fun draw(canvas: Canvas) {
        for ((bodyData, bodyViewData) in bodyViewDataMap) {

            canvas.save()

            canvas.translate(bodyData.position.x.toFloat(), bodyData.position.y.toFloat())
            canvas.rotate(Math.toDegrees(bodyData.rotation).toFloat())

            bodyData.shape.circleData?.also {
                canvas.drawCircle(
                        it.center.x.toFloat(),
                        it.center.y.toFloat(),
                        it.radius.toFloat(),
                        bodyViewData.paint
                )
            }
            bodyData.shape.rectangleData?.also {
                val hw = it.width / 2.0
                val hh = it.height / 2.0
                canvas.drawRect(
                        (it.center.x - hw).toFloat(),
                        (it.center.y + hh).toFloat(),
                        (it.center.x + hw).toFloat(),
                        (it.center.y - hh).toFloat(),
                        bodyViewData.paint
                )
            }

            canvas.restore()
        }
    }
}
