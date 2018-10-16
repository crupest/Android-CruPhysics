package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.utility.generateRandomColor

class AddBodyViewModel : ViewModel() {
    val shapeType: MutableLiveData<String> = MutableLiveData()
    val bodyType: MutableLiveData<String> = MutableLiveData()
    val density: MutableLiveData<Double> = MutableLiveData()
    val restitution: MutableLiveData<Double> = MutableLiveData()
    val friction: MutableLiveData<Double> = MutableLiveData()
    val bodyColor: MutableLiveData<Int> = MutableLiveData()

    init {
        bodyColor.value = generateRandomColor()
        density.value = 1.0
        friction.value = 0.2
    }
}
