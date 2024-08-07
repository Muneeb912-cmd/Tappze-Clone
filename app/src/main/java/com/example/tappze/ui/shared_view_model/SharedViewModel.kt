package com.example.tappze.com.example.tappze.ui.shared_view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tappze.com.example.tappze.broadcast.BatteryStatusObserver
import com.example.tappze.com.example.tappze.broadcast.NetworkStatusObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val networkStatusObserver: NetworkStatusObserver,
    private val batteryStatusObserver: BatteryStatusObserver
) : ViewModel() {

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    private val _batteryStatus = MutableLiveData<Float>()
    val batteryStatus: LiveData<Float> get() = _batteryStatus

    init {
        val networkStatusListener: (Boolean) -> Unit = { isConnected ->
            _networkStatus.postValue(isConnected)
        }
        val batteryStatusListener: (Float) -> Unit = { batteryPct ->
            _batteryStatus.postValue(batteryPct)
        }
        networkStatusObserver.addNetworkStatusListener(networkStatusListener)
        batteryStatusObserver.addBatteryStatusListener(batteryStatusListener)
        onCleared {
            networkStatusObserver.removeNetworkStatusListener(networkStatusListener)
            batteryStatusObserver.removeBatteryStatusListener(batteryStatusListener)
        }
    }
    private fun onCleared(action: () -> Unit) {
        action()
    }
}
