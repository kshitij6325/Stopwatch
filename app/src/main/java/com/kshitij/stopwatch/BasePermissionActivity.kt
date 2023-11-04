package com.kshitij.stopwatch

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class BasePermissionActivity : AppCompatActivity() {

    abstract val onPermissionGranted: () -> Unit
    abstract val onPermissionDenied: () -> Unit
    abstract val onShowPermissionExplanation: () -> Unit

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    fun askForPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, permission
            ) -> {
                onShowPermissionExplanation()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }

        }
    }
}