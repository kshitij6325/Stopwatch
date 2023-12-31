package com.kshitij.stopwatch.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kshitij.stopwatch.BuildConfig
import com.kshitij.stopwatch.stopwatch.core.StopwatchData

interface Logger {

    val tag: String

    fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("Stopwatch::$tag", message)
        }
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Long.toStopwatchData(): StopwatchData {
    val ms = this % 1000

    val sec = this / 1000
    val forSec = sec % 60

    val min = sec / 60
    val formin = min % 60

    val hr = min / 60
    val forHr = hr % 60

    return StopwatchData(forHr, formin, forSec, ms)
}

fun noOp() {
    /*NoOp*/
}


