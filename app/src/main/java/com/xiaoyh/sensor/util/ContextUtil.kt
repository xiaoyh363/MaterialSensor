package com.xiaoyh.sensor.util

import android.app.Application
import android.content.Context
import android.content.res.Resources

class ContextUtil : Application() {

    companion object {
        private var context : Application? = null
        fun getContext() : Context {
            return context!!
        }

        private fun getResource(): Resources {
            return getContext().resources
        }

        fun getString(id: Int): String {
            return getResource().getString(id)
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}