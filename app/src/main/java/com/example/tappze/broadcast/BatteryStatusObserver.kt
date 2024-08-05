package com.example.tappze.com.example.tappze.broadcast

interface BatteryStatusObserver {
    fun onBatteryStatusChanged(batteryPct: Float)
    fun addBatteryStatusListener(listener: (Float) -> Unit)
    fun removeBatteryStatusListener(listener: (Float) -> Unit)
}