package crupest.cruphysics.viewmodel

import androidx.lifecycle.MutableLiveData
import java.util.Objects

fun <T> MutableLiveData<T>.checkAndSetValue(value: T) {
    if (Objects.equals(this.value, value))
        this.value = value
}

fun <T> MutableLiveData<T>.setWhenNull(value: T) {
    if (this.value == null)
        this.value = value
}
