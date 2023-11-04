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
 * Class to manage stopwatch and handle various actions related to stopwatch.
 *
 * @param stopwatch: Instance of impl [Stopwatch].
 * @param notificationHandler: Instance of impl [StopWatchNotificationHandler].
 * @param onForegroundState: Callback invoked after [StopwatchAction.BACKGROUND].
 * @param onBackgroundSate: Callback invoked after [StopwatchAction.FOREGROUND].
 *
 * **/
class StopwatchManager(
    private val stopwatch: Stopwatch,
    private val notificationHandler: StopWatchNotificationHandler,
    private val onForegroundState: () -> Unit = {},
    private val onBackgroundSate: () -> Unit = {},
) {

    private var scope: CoroutineScope = CoroutineScope(SupervisorJob())

    /**
     * Observe this flow to get updated stopwatch time.
     *
     * **/
    val time = stopwatch.time

    /**
     * Observe this flow to get current stopwatch state.
     *
     * **/
    val state = stopwatch.state

    /**
     * Handles [StopwatchAction] passed from call site.
     *
     * **/
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

    /**
     * Update notification with [StopwatchData].
     *
     * **/
    fun updatedNotification(stopwatchData: StopwatchData): Notification {
        return notificationHandler.updateNotification(stopwatchData)
    }

    /**
     * Cleanup and reset stopwatch
     *
     * **/
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


/**
 * Enum class that denotes actions that can be taken from UI.
 *
 * */
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