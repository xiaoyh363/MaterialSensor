package com.xiaoyh.sensor

import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import com.xiaoyh.sensor.util.ToastUtil
import java.io.IOException

class ConnectAsyncTask(private val socket: BluetoothSocket, var listener: AsyncTaskListener?) :
    AsyncTask<Void, Void, Boolean>() {

    interface AsyncTaskListener {
        fun onSuccess(result: Boolean)
    }

    override fun onPreExecute() {
        ToastUtil.toast("开始连接")
    }

    override fun doInBackground(vararg params: Void): Boolean {
        return try {
            socket.connect()
            true
        } catch (e: IOException) {
            socket.close()
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        listener?.onSuccess(result)
    }
}