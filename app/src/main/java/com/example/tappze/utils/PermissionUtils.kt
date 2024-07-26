package com.example.tappze.com.example.tappze.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    private fun hasPermissions(activity: Activity, vararg permissions: String): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions(activity: Activity, requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun checkAndRequestPermissions(
        activity: Activity,
        requestCode: Int,
        vararg permissions: String
    ) {
        if (hasPermissions(activity, *permissions)) {
            // Permissions are already granted
            return
        }
        // Request permissions
        requestPermissions(activity, requestCode, *permissions)
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onPermissionsGranted: () -> Unit,
        onPermissionsDenied: () -> Unit
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
    }

    private const val REQUEST_CODE_PERMISSIONS = 1001
}