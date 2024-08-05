package com.example.tappze.com.example.tappze.ui.shared_view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tappze.com.example.tappze.broadcast.StatusObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val statusObserver: StatusObserver
) : ViewModel() {

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    private val _batteryStatus = MutableLiveData<Float>()
    val batteryStatus: LiveData<Float> get() = _batteryStatus

    init {
        statusObserver.addNetworkStatusListener { isConnected ->
            _networkStatus.postValue(isConnected)
        }
        statusObserver.addBatteryStatusListener { batteryPct ->
            _batteryStatus.postValue(batteryPct)
        }
    }

    override fun onCleared() {
        super.onCleared()
        statusObserver.removeNetworkStatusListener { isConnected ->
            _networkStatus.postValue(isConnected)
        }
        statusObserver.removeBatteryStatusListener { batteryPct ->
            _batteryStatus.postValue(batteryPct)
        }
    }
}