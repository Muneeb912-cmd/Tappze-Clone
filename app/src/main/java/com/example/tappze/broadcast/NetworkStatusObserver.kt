package com.example.tappze.com.example.tappze.broadcast

interface NetworkStatusObserver {
    fun onNetworkStatusChanged(isConnected: Boolean)
    fun addNetworkStatusListener(listener: (Boolean) -> Unit)
    fun removeNetworkStatusListener(listener: (Boolean) -> Unit)
}