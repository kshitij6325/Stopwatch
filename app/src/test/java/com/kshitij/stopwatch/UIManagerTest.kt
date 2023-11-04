package com.kshitij.stopwatch

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import com.kshitij.stopwatch.stopwatch.StopwatchAction
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.core.StopwatchState
import com.kshitij.stopwatch.stopwatch.service.StopwatchService
import com.kshitij.stopwatch.ui.UIManager
import com.kshitij.stopwatch.util.toStopwatchData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class UIManagerTest {

    private lateinit var scope: CoroutineScope
    private lateinit var sut: UIManager

    private var time: String = 0.toLong().toStopwatchData().toString()
    private var icon: Int = R.drawable.baseline_play_arrow_24

    @Before
    fun before() {
        scope = CoroutineScope(SupervisorJob())
        sut = UIManager(scope,
            stopWatchPauseCallback = ::pauseStopwatch,
            stopWatchStartCallback = ::startStopwatch,
            stopWatchForegroundCallback = ::moveToForeground,
            stopWatchBackgroundCallbacks = ::moveToBackground,
            onTimeChange = { time = it },
            onIconChange = { icon = it }
        )
    }

    @After
    fun after() {
        scope.cancel()
    }


    @Test
    fun `when in initial state, verify that onPlayPauseButtonClicked has correct callback`() {
        assert(sut.onPlayPauseButtonClicked == ::startStopwatch)
        assert(sut.onStartCallback() == Unit)
        assert(sut.onStopCallback() == Unit)
    }

    @Test
    fun `when start button is clicked, verify that onPlayPauseButtonClicked has correct callback`() {
        sut.stopwatchTimeFlow = flowOf(2000.toLong().toStopwatchData())
        sut.stopwatchStateFlow = flowOf(StopwatchState.STARTED)
        Thread.sleep(1000)
        assert(sut.onPlayPauseButtonClicked == ::pauseStopwatch)
        assert(time == 2000.toLong().toStopwatchData().toString())
        assert(icon == R.drawable.baseline_pause_24)
    }

    @Test
    fun `when pause button is clicked, verify that onPlayPauseButtonClicked has correct callback`() {
        sut.stopwatchTimeFlow = flowOf(2000.toLong().toStopwatchData())
        sut.stopwatchStateFlow = flowOf(StopwatchState.PAUSED)
        Thread.sleep(1000)
        assert(sut.onPlayPauseButtonClicked == ::startStopwatch)
        assert(time == 2000.toLong().toStopwatchData().toString())
        assert(icon == R.drawable.baseline_play_arrow_24)
    }

    @Test
    fun `when start button is clicked, verify that lifecycleCallbacks have correct callback`() {
        sut.stopwatchTimeFlow = flowOf(2000.toLong().toStopwatchData())
        sut.stopwatchStateFlow = flowOf(StopwatchState.STARTED)
        Thread.sleep(1000)
        assert(sut.onStartCallback == ::moveToForeground)
        assert(sut.onStopCallback == ::moveToBackground)
    }


    private fun startStopwatch() {
        /*NoOp*/
    }

    private fun pauseStopwatch() {
        /*NoOp*/
    }


    private fun moveToForeground() {
        /*NoOp*/
    }


    private fun moveToBackground() {
        /*NoOp*/
    }


}