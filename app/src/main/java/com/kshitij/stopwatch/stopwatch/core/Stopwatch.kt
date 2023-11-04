package com.kshitij.stopwatch.stopwatch.core

import kotlinx.coroutines.flow.Flow


/**
 * Stopwatch interface with stopwatch basic methods
 *
 * */
interface Stopwatch {

    /**
     * Observe this to get latest stopwatch time.
     *
     * */
    val time: Flow<StopwatchData>

    /**
     * Observe this to get current stopwatch state.
     *
     * */
    val state: Flow<StopwatchState>

    /**
     * Start stopwatch
     *
     * */
    fun start()

    /**
     * Reset stopwatch
     *
     * */
    fun reset()

    /**
     * Pause stopwatch
     *
     * */
    fun pause()
}


/**
 * Data class to hold time data for stopwatch.
 *
 * **/
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

/**
 * Enum depicting current Stopwatch state.
 *
 * **/
enum class StopwatchState {
    STARTED,
    PAUSED,
    RESET,
}