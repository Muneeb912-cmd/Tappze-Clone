package com.example.tappze.com.example.tappze.broadcast

class StatusObserverImp: StatusObserver {

    private var networkStatusListeners: MutableList<(Boolean) -> Unit> = mutableListOf()
    private var batteryStatusListeners: MutableList<(Float) -> Unit> = mutableListOf()

    override fun onNetworkStatusChanged(isConnected: Boolean) {
        networkStatusListeners.forEach { it(isConnected) }
    }

    override fun onBatteryStatusChanged(batteryPct: Float) {
        batteryStatusListeners.forEach { it(batteryPct) }
    }

    override fun addNetworkStatusListener(listener: (Boolean) -> Unit) {
        networkStatusListeners.add(listener)
    }

    override fun removeNetworkStatusListener(listener: (Boolean) -> Unit) {
        networkStatusListeners.remove(listener)
    }

    override fun addBatteryStatusListener(listener: (Float) -> Unit) {
        batteryStatusListeners.add(listener)
    }

    override fun removeBatteryStatusListener(listener: (Float) -> Unit) {
        batteryStatusListeners.remove(listener)
    }
}