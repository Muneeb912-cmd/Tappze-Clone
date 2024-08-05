package com.example.tappze.com.example.tappze.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

class StatusReceiver @Inject constructor(
    private val statusObserver: StatusObserver
) : BroadcastReceiver()  {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                val isConnected = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                statusObserver.onNetworkStatusChanged(isConnected)
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra("level", -1)
                val scale = intent.getIntExtra("scale", -1)
                val batteryPct = level / scale.toFloat() * 100
                statusObserver.onBatteryStatusChanged(batteryPct)
            }
        }
    }
}