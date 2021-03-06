package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateRectangleBodyViewModel : ViewModel() {
    val centerX: MutableLiveData<Double> = MutableLiveData()
    val centerY: MutableLiveData<Double> = MutableLiveData()
    val width: MutableLiveData<Double> = MutableLiveData()
    val height: MutableLiveData<Double> = MutableLiveData()
    val angle: MutableLiveData<Double> = MutableLiveData()
}
