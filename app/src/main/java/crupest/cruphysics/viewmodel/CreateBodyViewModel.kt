package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.physics.ShapeType

class CreateBodyViewModel : ViewModel() {
    val shapeType: MutableLiveData<ShapeType> = MutableLiveData()
}
