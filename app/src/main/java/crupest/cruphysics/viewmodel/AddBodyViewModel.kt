package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.serialization.data.BODY_TYPE_STATIC
import crupest.cruphysics.utility.RandomHelper

class AddBodyViewModel : ViewModel() {
    val shapeType: MutableLiveData<String> = mutableLiveData()
    val bodyType: MutableLiveData<String> = mutableLiveDataWithDefault(BODY_TYPE_STATIC)
    val density: MutableLiveData<Double> = mutableLiveDataWithDefault(1.0)
    val restitution: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val friction: MutableLiveData<Double> = mutableLiveDataWithDefault(0.2)
    val velocityX: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val velocityY: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val angularVelocity: MutableLiveData<Double> = mutableLiveDataWithDefault(0.0)
    val bodyColor: MutableLiveData<Int> = mutableLiveDataWithDefault(RandomHelper.generateRandomColor())
}
