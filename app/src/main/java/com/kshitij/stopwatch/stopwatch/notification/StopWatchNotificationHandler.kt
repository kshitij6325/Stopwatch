package com.kshitij.stopwatch.stopwatch.notification

import android.app.Notification
import com.kshitij.stopwatch.stopwatch.core.StopwatchData

interface StopWatchNotificationHandler {

    fun dismissNotification()

    fun updateNotification(stopwatchData: StopwatchData): Notification
}