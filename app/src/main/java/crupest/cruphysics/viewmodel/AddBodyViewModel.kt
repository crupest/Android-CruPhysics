package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.serialization.data.BODY_TYPE_STATIC
import crupest.cruphysics.utility.RandomHelper

class AddBodyViewModel : ViewModel() {
    val shapeType: MutableLiveData<String> = MutableLiveData()
    val bodyType: MutableLiveData<String> = MutableLiveData()
    val density: MutableLiveData<Double> = MutableLiveData()
    val restitution: MutableLiveData<Double> = MutableLiveData()
    val friction: MutableLiveData<Double> = MutableLiveData()
    val bodyColor: MutableLiveData<Int> = MutableLiveData()

    init {
        bodyType.value = BODY_TYPE_STATIC
        density.value = 1.0
        restitution.value = 0.0
        friction.value = 0.2
        bodyColor.value = RandomHelper.generateRandomColor()
    }
}
