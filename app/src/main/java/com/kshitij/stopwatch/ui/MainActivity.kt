package com.kshitij.stopwatch.ui

import android.Manifest
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.kshitij.stopwatch.BasePermissionActivity
import com.kshitij.stopwatch.R
import com.kshitij.stopwatch.stopwatch.service.StopwatchService
import com.kshitij.stopwatch.util.Logger
import com.kshitij.stopwatch.util.toast

class MainActivity : BasePermissionActivity(), Logger {

    override val tag: String
        get() = "MainActivity"

    private val uiManager = UIManager(lifecycleScope,
        stopWatchPauseCallback = ::pauseStopwatch,
        stopWatchStartCallback = ::startStopwatchWithPermission,
        stopWatchForegroundCallback = ::moveToForeground,
        stopWatchBackgroundCallbacks = ::moveToBackground,
        onTimeChange = {
            findViewById<TextView>(R.id.hr).text = it
        },
        onIconChange = {
            findViewById<ImageView>(R.id.play).setImageResource(it)
        }
    )

    override val onPermissionGranted: () -> Unit = ::startStopwatch

    override val onPermissionDenied: () -> Unit = {
        toast("Grant notification permission. Stopwatch notification won't be shown on background")
        startStopwatch()
    }

    override val onShowPermissionExplanation: () -> Unit = onPermissionDenied

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.play).setOnClickListener {
            uiManager.onPlayPauseButtonClicked()
        }

        findViewById<ImageView>(R.id.reset).setOnClickListener {
            StopwatchService.reset(this)
        }
    }

    override fun onStart() {
        super.onStart()
        uiManager.onStartCallback()
    }

    override fun onStop() {
        super.onStop()
        uiManager.onStopCallback()
    }

    private val connection = StopwatchServiceConnection {
        uiManager.stopwatchTimeFlow = it?.time
        uiManager.stopwatchStateFlow = it?.state
    }

    private fun startStopwatchWithPermission() {
        askForPermission(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun startStopwatch() {
        StopwatchService.start(this)
        moveToForeground()
    }

    private fun pauseStopwatch() {
        StopwatchService.pause(this)
    }


    private fun moveToForeground() {
        StopwatchService.moveToForeground(this, connection)
    }


    private fun moveToBackground() {
        StopwatchService.moveToBackground(this, connection)
    }
}