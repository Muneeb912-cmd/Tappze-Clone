package com.example.tappze.com.example.tappze.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import javax.inject.Inject

class BatteryStatusReceiver @Inject constructor(
    private val batteryStatusObserver: BatteryStatusObserver
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BATTERY_CHANGED == intent.action) {
            val level = intent.getIntExtra("level", -1)
            val scale = intent.getIntExtra("scale", -1)
            val batteryPct = level / scale.toFloat() * 100

            // Notify ViewModel or any observer about battery status
            batteryStatusObserver.onBatteryStatusChanged(batteryPct)
        }
    }
}