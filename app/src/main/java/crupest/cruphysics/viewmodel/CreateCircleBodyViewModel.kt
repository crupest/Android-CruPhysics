package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateCircleBodyViewModel : ViewModel() {
    val centerX: MutableLiveData<Double> = MutableLiveData()
    val centerY: MutableLiveData<Double> = MutableLiveData()
    val radius: MutableLiveData<Double> = MutableLiveData()
    val angle: MutableLiveData<Double> = MutableLiveData()
}
