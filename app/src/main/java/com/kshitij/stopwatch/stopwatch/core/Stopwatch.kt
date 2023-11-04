package com.kshitij.stopwatch.stopwatch.core

import kotlinx.coroutines.flow.Flow

interface Stopwatch {

    val time: Flow<StopwatchData>

    val state: Flow<StopwatchState>

    fun start()

    fun reset()

    fun pause()
}

data class StopwatchData(
    val hr: Long = 0,
    val min: Long = 0,
    val sec: Long = 0,
    val milliSec: Long = 0
) {

    override fun toString(): String {
        return "${getAppText(hr)}:${getAppText(min)}:${getAppText(sec)}"
    }

    private fun getAppText(value: Long): String {
        return if (value >= 10) value.toString() else "0$value"
    }
}

enum class StopwatchState {
    STARTED,
    PAUSED,
    RESET,
}