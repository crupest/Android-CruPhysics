package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.physics.BodyType
import crupest.cruphysics.physics.ShapeType
import crupest.cruphysics.utility.RandomHelper

class AddBodyViewModel : ViewModel() {
    val shapeType: MutableLiveData<ShapeType> = mutableLiveData()
    val bodyType: MutableLiveData<BodyType> = mutableLiveDataWithDefault(BodyType.STATIC)
    val density: MutableLiveData<Double> = mutableLiveDataWithDefault(1.0)
    val restitution: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val friction: MutableLiveData<Double> = mutableLiveDataWithDefault(0.2)
    val velocityX: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val velocityY: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val angularVelocity: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val bodyColor: MutableLiveData<Int> = mutableLiveDataWithDefault(RandomHelper.generateRandomColor())
}
