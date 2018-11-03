package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.physics.BodyType
import crupest.cruphysics.utility.RandomHelper

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
}
