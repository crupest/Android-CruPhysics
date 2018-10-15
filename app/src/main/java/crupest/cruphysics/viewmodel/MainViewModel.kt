package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import crupest.cruphysics.serialization.data.CameraData

class MainViewModel: ViewModel() {
    val camera: MutableLiveData<CameraData> = MutableLiveData()
}
