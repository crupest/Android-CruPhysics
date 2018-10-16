package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddRectangleBodyViewModel : ViewModel() {
    var init: Boolean = false
    val centerX: MutableLiveData<Double> = MutableLiveData()
    val centerY: MutableLiveData<Double> = MutableLiveData()
    val width: MutableLiveData<Double> = MutableLiveData()
    val height: MutableLiveData<Double> = MutableLiveData()
    val angle: MutableLiveData<Double> = MutableLiveData()

    init {
        centerX.value = 0.0
        centerY.value = 0.0
        width.value = 0.0
        height.value = 0.0
        angle.value = 0.0
    }
}
