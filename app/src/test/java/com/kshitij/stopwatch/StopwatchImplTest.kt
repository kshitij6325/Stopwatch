package com.kshitij.stopwatch

import com.kshitij.stopwatch.stopwatch.core.Stopwatch
import com.kshitij.stopwatch.stopwatch.core.StopwatchData
import com.kshitij.stopwatch.stopwatch.core.StopwatchImpl
import com.kshitij.stopwatch.stopwatch.core.StopwatchState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test

class StopwatchImplTest {

    private lateinit var sut: Stopwatch

    @Before
    fun before() {
        sut = StopwatchImpl()
    }

    @Test
    fun `when no method is called, check that stopwatch state is reset`() {
        assert(sut.state is MutableStateFlow)
        assert((sut.state as MutableStateFlow).value == StopwatchState.RESET)
    }

    @Test
    fun `when start is called, check that stopwatch state is started`() {
        sut.start()
        assert(sut.state is MutableStateFlow)
        assert((sut.state as MutableStateFlow).value == StopwatchState.STARTED)
        Thread.sleep(2000)
        assert((sut.time as MutableStateFlow).value.sec >= 1)
    }

    @Test
    fun `when pause is called after start, check that stopwatch state is paused`() {
        sut.start()
        Thread.sleep(3000)
        sut.pause()

        assert(sut.state is MutableStateFlow)
        assert((sut.state as MutableStateFlow).value == StopwatchState.PAUSED)

        val pausedVal = (sut.time as MutableStateFlow).value.sec
        Thread.sleep(3000)
        val currentVal = (sut.time as MutableStateFlow).value.sec
        assert(pausedVal == currentVal)
    }

    @Test
    fun `when pause is called before start, check that stopwatch state is not paused`() {
        sut.pause()
        assert(sut.state is MutableStateFlow)
        assert((sut.state as MutableStateFlow).value != StopwatchState.PAUSED)
    }

    @Test
    fun `when reset is called after start, check that stopwatch state is reset`() {
        sut.start()
        sut.reset()
        assert(sut.state is MutableStateFlow)
        assert((sut.state as MutableStateFlow).value == StopwatchState.RESET)
        assert((sut.time as MutableStateFlow).value == StopwatchData(0, 0, 0, 0))
    }

    @Test
    fun `when start is called twice, check that no exception`() {
        try {
            sut.start()
            sut.start()
            assert((sut.state as MutableStateFlow).value == StopwatchState.STARTED)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `when reset is called twice, check that no exception`() {
        try {
            sut.start()
            sut.reset()
            sut.reset()
            assert((sut.state as MutableStateFlow).value == StopwatchState.RESET)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `when pause is called twice, check that no exception`() {
        try {
            sut.start()
            sut.pause()
            sut.pause()
            assert((sut.state as MutableStateFlow).value == StopwatchState.PAUSED)
        } catch (e: Exception) {
            assert(false)
        }
    }

}