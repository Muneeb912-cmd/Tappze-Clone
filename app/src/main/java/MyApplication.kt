package com.example.tappze

// MyApplication.kt
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import com.example.tappze.com.example.tappze.broadcast.BatteryStatusReceiver
import com.example.tappze.com.example.tappze.broadcast.NetworkStatusReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var networkStatusReceiver: NetworkStatusReceiver

    @Inject
    lateinit var batteryStatusReceiver: BatteryStatusReceiver

    override fun onCreate() {
        super.onCreate()

        // Register receivers
        val networkFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkStatusReceiver, networkFilter)

        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStatusReceiver, batteryFilter)
    }

    override fun onTerminate() {
        super.onTerminate()
        // Unregister receivers when the app terminates
        unregisterReceiver(networkStatusReceiver)
        unregisterReceiver(batteryStatusReceiver)
    }
}

