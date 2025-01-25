package com.example.babycare.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionManager @Inject constructor(
    private val context: Context
) {
    fun checkAndRequestPermissions(
        activity: ComponentActivity,
        permissions: Array<String>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            if (results.all { it.value }) {
                onGranted()
            } else {
                onDenied()
            }
        }

        if (permissions.all { 
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED 
        }) {
            onGranted()
        } else {
            launcher.launch(permissions)
        }
    }
} 