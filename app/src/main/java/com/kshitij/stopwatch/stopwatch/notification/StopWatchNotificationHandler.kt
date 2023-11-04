package com.kshitij.stopwatch.stopwatch.notification

import android.app.Notification
import com.kshitij.stopwatch.stopwatch.core.StopwatchData

/**
 * Interface to manager Notification for stopwatch once app goes to background.
 *
 * */
interface StopWatchNotificationHandler {


    /**
     * Clear notification from the tray.
     *
     * */
    fun dismissNotification()

    /**
     * Update notification with stopwatch time.
     *
     * */
    fun updateNotification(stopwatchData: StopwatchData): Notification
}