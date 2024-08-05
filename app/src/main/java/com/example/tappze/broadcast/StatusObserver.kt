package com.example.tappze.com.example.tappze.broadcast

interface StatusObserver {
    fun onNetworkStatusChanged(isConnected: Boolean)
    fun onBatteryStatusChanged(batteryPct: Float)
    fun addNetworkStatusListener(listener: (Boolean) -> Unit)
    fun removeNetworkStatusListener(listener: (Boolean) -> Unit)
    fun addBatteryStatusListener(listener: (Float) -> Unit)
    fun removeBatteryStatusListener(listener: (Float) -> Unit)
}