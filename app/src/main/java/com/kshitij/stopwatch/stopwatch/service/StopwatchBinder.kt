package com.kshitij.stopwatch.stopwatch.service

import android.os.Binder

class StopwatchBinder(private val service: StopwatchService) : Binder() {

    fun getService(): StopwatchService = service
}