package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.utility.generateRandomColor

class AddBodyViewModel : ViewModel() {
    val shapeType: MutableLiveData<Int> = MutableLiveData()
    val bodyColor: MutableLiveData<Int> = MutableLiveData()

    init {
        bodyColor.value = generateRandomColor()
    }
}
