package com.kshitij.stopwatch.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kshitij.stopwatch.R
import com.kshitij.stopwatch.stopwatch.core.StopwatchState
import com.kshitij.stopwatch.stopwatch.service.StopwatchServiceConnection
import com.kshitij.stopwatch.stopwatch.service.StopwatchService
import com.kshitij.stopwatch.util.Logger
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

//TODO: Ask for notification permission
//TODO: Fix notification bug
class MainActivity : AppCompatActivity(), Logger {

    override val tag: String
        get() = "MainActivity"

    private val uiManager = UIManager(lifecycleScope,
        stopWatchPauseCallback = ::pauseStopwatch,
        stopWatchStartCallback = ::startStopwatch,
        stopWatchForegroundCallback = ::moveToForeground,
        stopWatchBackgroundCallbacks = ::moveToBackground,
        onTimeChange = {
            findViewById<TextView>(R.id.hr).text = it
        },
        onIconChange = {
            findViewById<ImageView>(R.id.play).setImageResource(it)
        }
    )

    private val connection = StopwatchServiceConnection {
        uiManager.stopwatchTimeFlow = it?.time
        uiManager.stopwatchStateFlow = it?.state
    }

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