package com.kshitij.stopwatch.stopwatch.service

import android.os.Binder


/**
 * Binder class to be returned from [StopwatchService.onBind] method.
 *
 * */
class StopwatchBinder(private val service: StopwatchService) : Binder() {

    fun getService(): StopwatchService = service
}