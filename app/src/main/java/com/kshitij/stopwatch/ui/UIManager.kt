package com.kshitij.stopwatch.ui

import com.kshitij.stopwatch.R
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.core.StopwatchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UIManager(
    private val scope: CoroutineScope,
    private val stopWatchPauseCallback: () -> Unit,
    private val stopWatchStartCallback: () -> Unit,
    private val stopWatchForegroundCallback: () -> Unit,
    private val stopWatchBackgroundCallbacks: () -> Unit,
    private val onTimeChange: (String) -> Unit,
    private val onIconChange: (Int) -> Unit
) {

    var onPlayPauseButtonClicked: () -> Unit = stopWatchStartCallback
        private set
    var onStartCallback: () -> Unit = {}
        private set
    var onStopCallback: () -> Unit = {}
        private set

    var stopwatchTimeFlow: Flow<StopwatchData>? = null
        set(value) {
            field = value
            scope.launch {
                field?.collect {
                    onTimeChange(it.toString())
                }
            }
        }

    var stopwatchStateFlow: Flow<StopwatchState>? = null
        set(value) {
            field = value
            scope.launch {
                field?.collect {
                    when (it) {
                        StopwatchState.STARTED -> {
                            onPlayPauseButtonClicked = stopWatchPauseCallback
                            onStartCallback = stopWatchForegroundCallback
                            onStopCallback = stopWatchBackgroundCallbacks
                            onIconChange(R.drawable.baseline_pause_24)
                        }

                        else -> {
                            onPlayPauseButtonClicked = stopWatchStartCallback
                            onStartCallback = {}
                            onStopCallback = {}
                            onIconChange(R.drawable.baseline_play_arrow_24)
                        }
                    }
                }
            }
        }

}