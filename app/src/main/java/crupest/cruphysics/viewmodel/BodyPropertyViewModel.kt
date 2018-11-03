package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.physics.BodyType
import crupest.cruphysics.physics.BodyUserData
import crupest.cruphysics.physics.checkAndGetFixture
import crupest.cruphysics.physics.cruUserData
import crupest.cruphysics.utility.RandomHelper
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Vector2

class BodyPropertyViewModel : ViewModel() {
    val bodyType: MutableLiveData<BodyType> = MutableLiveData()
    val density: MutableLiveData<Double> = MutableLiveData()
    val restitution: MutableLiveData<Double> = MutableLiveData()
    val friction: MutableLiveData<Double> = MutableLiveData()
    val velocityX: MutableLiveData<Double> = MutableLiveData()
    val velocityY: MutableLiveData<Double> = MutableLiveData()
    val angularVelocity: MutableLiveData<Double> = MutableLiveData()
    val bodyColor: MutableLiveData<Int> = MutableLiveData()

    fun initDefault() {
        bodyType.value = BodyType.STATIC
        density.value = 1.0
        restitution.value = 0.0
        friction.value = 0.2
        velocityX.value = 0.0
        velocityY.value = 0.0
        angularVelocity.value = 0.0
        bodyColor.value = RandomHelper.generateRandomColor()
    }

    fun fromBody(body: Body) {
        bodyType.value = BodyType.fromMassType(body.mass.type)
        val fixture = body.checkAndGetFixture()
        density.value = fixture.density
        restitution.value = fixture.restitution
        friction.value = fixture.friction
        velocityX.value = body.linearVelocity.x
        velocityY.value = body.linearVelocity.y
        angularVelocity.value = body.angularVelocity
        bodyColor.value = body.cruUserData.color
    }

    fun writeToBody(body: Body) {
        val fixture = body.checkAndGetFixture()
        fixture.density = density.value!!
        fixture.restitution = restitution.value!!
        fixture.friction = friction.value!!

        body.setMass(bodyType.value!!.massType)

        body.linearVelocity = Vector2(velocityX.value!!, velocityY.value!!)
        body.angularVelocity = angularVelocity.value!!

        body.userData = BodyUserData(body, bodyColor.value!!)
    }

    fun createBody(shape: Convex, position: Vector2, angle: Double): Body {
        val body = Body()

        body.addFixture(shape, density.value!!, friction.value!!, restitution.value!!)
        body.translate(position)
        body.rotateAboutCenter(angle)

        body.setMass(bodyType.value!!.massType)

        body.linearVelocity = Vector2(velocityX.value!!, velocityY.value!!)
        body.angularVelocity = angularVelocity.value!!

        body.userData = BodyUserData(body, bodyColor.value!!)

        return body
    }
}
