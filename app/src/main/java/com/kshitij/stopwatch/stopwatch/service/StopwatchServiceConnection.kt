package com.kshitij.stopwatch.stopwatch.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.kshitij.stopwatch.stopwatch.service.StopwatchBinder
import com.kshitij.stopwatch.stopwatch.service.StopwatchService
import com.kshitij.stopwatch.util.Logger

class StopwatchServiceConnection(private val onConnectionEstablished: (StopwatchService?) -> Unit) :
    ServiceConnection, Logger {

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        log("onServiceConnected")
        val connectedService = (service as? StopwatchBinder)?.getService()
        onConnectionEstablished(connectedService)

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        log("onServiceDisconnected")
    }

    override val tag: String
        get() = "StopwatchServiceConnection"
}