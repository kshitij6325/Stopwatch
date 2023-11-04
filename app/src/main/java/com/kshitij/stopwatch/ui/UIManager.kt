package com.kshitij.stopwatch.ui

import androidx.activity.result.contract.ActivityResultContracts.*
import com.kshitij.stopwatch.R
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.core.StopwatchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Class that contains logic for UI.
 *
 * @param scope: [CoroutineScope] for observing stopwatch data.
 * @param stopWatchPauseCallback: Callback invoked to pause stopwatch from UI.
 * @param stopWatchStartCallback: Callback invoked to start stopwatch from UI.
 * @param stopWatchForegroundCallback: Callback invoked when UI comes to foreground.
 * @param stopWatchBackgroundCallbacks: Callback invoked when UI goes to background.
 * @param onTimeChange: Callback invoked when stopwatch time changes.
 * @param onIconChange: Callback invoked on Stopwatch state change with play/pause icon data.
 *
 * */
class UIManager(
    private val scope: CoroutineScope,
    private val stopWatchPauseCallback: () -> Unit,
    private val stopWatchStartCallback: () -> Unit,
    private val stopWatchForegroundCallback: () -> Unit,
    private val stopWatchBackgroundCallbacks: () -> Unit,
    private val onTimeChange: (String) -> Unit,
    private val onIconChange: (Int) -> Unit,
) {

    /**
     * Invoke this on UI to call when play/pause button is clicked.
     *
     * */
    var onPlayPauseButtonClicked: () -> Unit = stopWatchStartCallback
        private set

    /**
     * Invoke this on UI to call when UI comes to foreground.
     *
     * */
    var onStartCallback: () -> Unit = stopWatchForegroundCallback
        private set

    /**
     * Invoke this on UI to call when UI goes to background.
     *
     * */
    var onStopCallback: () -> Unit = stopWatchBackgroundCallbacks
        private set

    /**
     * Set this on UI to get the time flow for stopwatch.
     *
     * */
    var stopwatchTimeFlow: Flow<StopwatchData>? = null
        set(value) {
            field = value
            scope.collect(field) {
                onTimeChange(it.toString())
            }
        }

    /**
     * Set this on UI to get the state flow for stopwatch.
     *
     * */
    var stopwatchStateFlow: Flow<StopwatchState>? = null
        set(value) {
            field = value
            scope.collect(field) {
                when (it) {
                    StopwatchState.STARTED -> {
                        handleStartedState()
                    }

                    else -> {
                        handleNonStartedState()
                    }
                }
            }
        }

    private fun handleStartedState() {
        onPlayPauseButtonClicked = stopWatchPauseCallback
        onIconChange(R.drawable.baseline_pause_24)
    }

    private fun handleNonStartedState() {
        onPlayPauseButtonClicked = stopWatchStartCallback
        onIconChange(R.drawable.baseline_play_arrow_24)
    }

    private fun <T> CoroutineScope.collect(flow: Flow<T>?, callback: (T) -> Unit) {
        launch {
            flow?.collectLatest {
                callback(it)
            }
        }
    }

}