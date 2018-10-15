package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import java.util.Objects

fun <T> MutableLiveData<T>.checkAndSetValue(value: T) {
    if (Objects.equals(this.value, value))
        this.value = value
}
