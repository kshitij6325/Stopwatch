package com.kshitij.stopwatch.stopwatch

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import com.kshitij.stopwatch.stopwatch.core.Stopwatch
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.core.StopwatchImpl
import com.kshitij.stopwatch.stopwatch.notification.StopWatchNotificationHandler
import com.kshitij.stopwatch.stopwatch.notification.StopWatchNotificationHandlerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *
 *
 * **/
class StopwatchManager(
    private val stopwatch: Stopwatch,
    private val notificationHandler: StopWatchNotificationHandler,
    private val onForegroundState: () -> Unit = {},
    private val onBackgroundSate: () -> Unit = {},
) {

    private var scope: CoroutineScope = CoroutineScope(SupervisorJob())

    val time = stopwatch.time
    val state = stopwatch.state

    fun onAction(action: StopwatchAction) {
        when (action) {

            StopwatchAction.FOREGROUND -> {
                stopObservingTime()
                onForegroundState()
            }

            StopwatchAction.BACKGROUND -> {
                observeStopwatchTimeForNotification()
                onBackgroundSate()
            }

            StopwatchAction.START -> {
                stopwatch.start()
            }

            StopwatchAction.PAUSE -> {
                stopwatch.pause()
            }

            StopwatchAction.RESET -> {
                stopwatch.reset()
            }
        }
    }

    fun updatedNotification(stopwatchData: StopwatchData): Notification {
        return notificationHandler.updateNotification(stopwatchData)
    }

    fun cleanup() {
        stopObservingTime()
        stopwatch.reset()
    }

    private fun observeStopwatchTimeForNotification() {
        scope = CoroutineScope(SupervisorJob())
        scope.launch {
            stopwatch.time.collect {
                notificationHandler.updateNotification(it)
            }
        }
    }

    private fun stopObservingTime() {
        scope.cancel()
    }

    companion object {
        fun get(
            context: Context,
            onForegroundState: () -> Unit,
            onBackgroundSate: () -> Unit
        ): StopwatchManager {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationHandler = StopWatchNotificationHandlerImpl(context, notificationManager)
            return StopwatchManager(
                StopwatchImpl(),
                notificationHandler,
                onForegroundState,
                onBackgroundSate
            )
        }
    }

}

enum class StopwatchAction(private val tag: String) {
    FOREGROUND("action-foreground"),
    BACKGROUND("action-background"),
    START("action-start"),
    PAUSE("action-pause"),
    RESET("action-reset");

    companion object {
        fun fromString(action: String): StopwatchAction {
            return when (action) {
                FOREGROUND.name -> FOREGROUND
                BACKGROUND.name -> BACKGROUND
                START.name -> START
                PAUSE.name -> PAUSE
                else -> RESET
            }
        }
    }

}