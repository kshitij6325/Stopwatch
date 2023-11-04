package com.kshitij.stopwatch.stopwatch.core

import com.kshitij.stopwatch.util.toStopwatchData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Timer

import kotlin.concurrent.timerTask

/**
 * Implementation of [Stopwatch] using [Timer] class.
 *
 * */
class StopwatchImpl : Stopwatch {

    override val time: Flow<StopwatchData> = MutableStateFlow(StopwatchData())
    override val state: Flow<StopwatchState> = MutableStateFlow(StopwatchState.RESET)

    private var elapsedTime = 0L
    private var timer = Timer()

    private fun newTask() = timerTask {
        elapsedTime++
        (time as MutableStateFlow).tryEmit(elapsedTime.toStopwatchData())
    }

    override fun start() {
        if ((state as MutableStateFlow).value != StopwatchState.STARTED) {
            timer = Timer()
            timer.schedule(newTask(), 1, 1)
            state.tryEmit(StopwatchState.STARTED)
        }
    }

    override fun reset() {
        if ((state as MutableStateFlow).value != StopwatchState.RESET) {
            elapsedTime = 0
            timer.cancel()
            state.tryEmit(StopwatchState.RESET)
            (time as MutableStateFlow).tryEmit(StopwatchData())
        }
    }

    override fun pause() {
        if ((state as MutableStateFlow).value == StopwatchState.STARTED) {
            timer.cancel()
            state.tryEmit(StopwatchState.PAUSED)
        }
    }
}