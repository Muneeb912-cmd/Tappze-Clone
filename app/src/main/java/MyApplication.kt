package com.example.tappze

// MyApplication.kt
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.example.tappze.com.example.tappze.broadcast.StatusReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var networkAndBatteryStatusReceiver: StatusReceiver

    override fun onCreate() {
        super.onCreate()

        val networkFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkAndBatteryStatusReceiver, networkFilter)

        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(networkAndBatteryStatusReceiver, batteryFilter)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(networkAndBatteryStatusReceiver)
    }
}

