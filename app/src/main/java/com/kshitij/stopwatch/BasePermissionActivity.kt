package com.kshitij.stopwatch

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class BasePermissionActivity : AppCompatActivity() {

    abstract val onGranted: () -> Unit
    abstract val onDenied: () -> Unit
    open val onExplaination: () -> Unit = {}

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onGranted()
            } else {
                onDenied()
            }
        }

    fun askForPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, permission
            ) -> {
                onExplaination()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }

        }
    }
}