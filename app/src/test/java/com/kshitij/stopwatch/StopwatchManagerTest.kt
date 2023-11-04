package com.kshitij.stopwatch

import com.kshitij.stopwatch.stopwatch.StopwatchAction
import com.kshitij.stopwatch.stopwatch.StopwatchManager
import com.kshitij.stopwatch.stopwatch.core.Stopwatch
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.notification.StopWatchNotificationHandler
import com.kshitij.stopwatch.util.toStopwatchData
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@RunWith(MockitoJUnitRunner::class)
class StopwatchManagerTest {

    private var stopwatch = mock<Stopwatch>()
    private val notificationHandler = mock<StopWatchNotificationHandler>()
    private var isForeground = false
    private var isBackground = false
    private lateinit var sut: StopwatchManager

    @Before
    fun before() {
        sut = StopwatchManager(stopwatch, notificationHandler, {
            isForeground = true
            isBackground = false
        }, {
            isForeground = false
            isBackground = true
        })
    }

    @Test
    fun `when stopwatch actions, verify if correct stopwatch methods are called`() {
        sut.onAction(StopwatchAction.START)
        verify(stopwatch).start()

        sut.onAction(StopwatchAction.PAUSE)
        verify(stopwatch).pause()

        sut.onAction(StopwatchAction.RESET)
        verify(stopwatch).reset()
    }


    @Test
    fun `when action background, verify if updatedNotification is called with correct data`() {
        whenever(stopwatch.time).thenReturn(flowOf(1000.toLong().toStopwatchData()))
        sut.onAction(StopwatchAction.BACKGROUND)
        Thread.sleep(2000)
        assert(!isForeground && isBackground)
        verify(notificationHandler).updateNotification(1000.toLong().toStopwatchData())
    }

    @Test
    fun `when updatedNotification is called, verify correct notificationHandler method is invoked`() {
        sut.updatedNotification(StopwatchData(0, 0, 0))
        verify(notificationHandler).updateNotification(StopwatchData(0, 0, 0))
    }

    @Test
    fun `when cleanup is called, verify stopwatch is reset `() {
        sut.cleanup()
        verify(stopwatch).reset()
    }
}