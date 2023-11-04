package com.kshitij.stopwatch.stopwatch.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kshitij.stopwatch.ui.MainActivity
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.util.Logger

private const val CHANNEL_ID = "Stopwatch_notification_id"
const val NOTIFICATION_ID = 998


/**
 * Impl class for [StopWatchNotificationHandler] using [NotificationManager].
 *
 * */
class StopWatchNotificationHandlerImpl(
    private val context: Context,
    private val notificationManager: NotificationManager
) : StopWatchNotificationHandler, Logger {

    override val tag: String
        get() = "StopWatchNotificationHandlerImpl"

    private val notificationBuilder by lazy {
        getNotification()
    }

    init {
        buildNotificationChannel()
    }

    private fun getNotification(): NotificationCompat.Builder {

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(androidx.core.R.drawable.notification_action_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
    }

    //Registering notification channel for API >= 26
    private fun buildNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stopwatch notification channel"
            val descriptionText =
                "Enable stopwatch current time and actions in the notification tray"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun dismissNotification() {
        log("dismiss notification")
        notificationManager.cancel(NOTIFICATION_ID)
    }

    override fun updateNotification(stopwatchData: StopwatchData): Notification {
        log("update notification")
        val notification = notificationBuilder.setContentText(stopwatchData.toString()).build()
        notificationManager.notify(NOTIFICATION_ID, notification)
        return notification
    }


}