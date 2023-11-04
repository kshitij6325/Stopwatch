package com.kshitij.stopwatch

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.notification.StopWatchNotificationHandlerImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class StopwatchNotificationHandlerTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var sut: StopWatchNotificationHandlerImpl

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sut = StopWatchNotificationHandlerImpl(context, notificationManager)
    }

    @Test
    fun `given updateNotification is called, check if notification is created`() {
        val notification = sut.updateNotification(StopwatchData(0, 0, 0))
        assert(notification.contentIntent != null)
        assert(notification.flags == 2)
    }

    @Test
    fun `given dismissNotification is called, check if notification is cancelled`() {
        sut.updateNotification(StopwatchData(0, 0, 0))
        sut.dismissNotification()

    }
}