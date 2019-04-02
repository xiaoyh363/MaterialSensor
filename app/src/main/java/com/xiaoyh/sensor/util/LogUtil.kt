package com.xiaoyh.sensor.util

import android.util.Log

object LogUtil {
    private const val tag = "xiaoyh.sensor.log"

    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5
    private const val NOTHING = 6

    private const val LEVEL = VERBOSE

    fun v(msg: String) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    fun d(msg: String) {
        if (LEVEL <= VERBOSE) {
            Log.d(tag, msg)
        }
    }

    fun i(msg: String) {
        if (LEVEL <= VERBOSE) {
            Log.i(tag, msg)
        }
    }

    fun w(msg: String) {
        if (LEVEL <= VERBOSE) {
            Log.w(tag, msg)
        }
    }

    fun e(msg: String) {
        if (LEVEL <= VERBOSE) {
            Log.e(tag, msg)
        }
    }
}