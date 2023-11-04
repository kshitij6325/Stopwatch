package com.kshitij.stopwatch.stopwatch.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.kshitij.stopwatch.stopwatch.StopwatchAction
import com.kshitij.stopwatch.stopwatch.StopwatchManager
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.core.StopwatchState
import com.kshitij.stopwatch.stopwatch.notification.NOTIFICATION_ID
import com.kshitij.stopwatch.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Service in which stopwatch runs. Other components will observe the [time] from this service and
 * display on UI.
 *
 * When app is in foreground --> [Bound service] MainActivity binds to this service and listens for stopwatch update.
 * When app is in background --> [Foreground service] shows notification for stopwatch time updates.
 *
 * */
class StopwatchService : Service(), Logger {

    var time: Flow<StopwatchData>? = null
    var state: Flow<StopwatchState>? = null

    private val binder by lazy { StopwatchBinder(this) }

    private lateinit var stopwatchManager: StopwatchManager

    override val tag: String
        get() = "StopwatchService"

    override fun onCreate() {
        super.onCreate()
        stopwatchManager =
            StopwatchManager.get(
                this,
                onForegroundState = ::dismissNotification,
                onBackgroundSate = ::showForegroundNotification
            )
        time = stopwatchManager.time
        state = stopwatchManager.state
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { stopwatchManager.onAction(StopwatchAction.fromString(it)) }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopwatchManager.cleanup()
    }

    private fun dismissNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun showForegroundNotification() {
        startForeground(
            NOTIFICATION_ID,
            stopwatchManager.updatedNotification((time as StateFlow<StopwatchData>).value)
        )
    }

    companion object : Logger {

        override val tag: String
            get() = "StopwatchService"

        fun start(context: Context) {
            startService(context, StopwatchAction.START.name)
        }

        fun pause(context: Context) {
            startService(context, StopwatchAction.PAUSE.name)
        }

        fun reset(context: Context) {
            startService(context, StopwatchAction.RESET.name)
        }

        fun moveToForeground(context: Context, connection: ServiceConnection) {
            val intent = intent(context, StopwatchAction.FOREGROUND.name)
            context.startService(intent)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        fun moveToBackground(context: Context, connection: ServiceConnection) {
            context.unbindService(connection)
            val intent = intent(context, StopwatchAction.BACKGROUND.name)
            ContextCompat.startForegroundService(context, intent)
        }

        private fun startService(context: Context, action: String) {
            context.startService(intent(context, action))
        }

        private fun intent(context: Context, action: String): Intent {
            return Intent(context, StopwatchService::class.java).apply {
                this.action = action
            }
        }
    }

}