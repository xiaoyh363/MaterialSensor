package com.xiaoyh.sensor.listener

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothSocket
import android.graphics.Color
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.xiaoyh.sensor.MainActivity.Companion.ba
import com.xiaoyh.sensor.MainActivity.Companion.mBlueList
import com.xiaoyh.sensor.ConnectAsyncTask
import com.xiaoyh.sensor.R
import com.xiaoyh.sensor.util.ToastUtil
import java.util.*

class MyListViewListener(
    private val activity: Activity,
    private var socket: BluetoothSocket?,
    private var connectAsyncTask: ConnectAsyncTask?
) : AdapterView.OnItemClickListener {

    private val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val device = mBlueList[position]
        val dialog = AlertDialog.Builder(activity)
            .setTitle("${device.name} / ${device.address}")
            .setMessage("您确定要连接这个吗？")
            .setPositiveButton("确定") { _, _ ->
                if (ba.isDiscovering) {
                    ba.cancelDiscovery()
                }
                socket = device.createRfcommSocketToServiceRecord(myUUID)
                connectAsyncTask = ConnectAsyncTask(socket!!, object : ConnectAsyncTask.AsyncTaskListener {
                    override fun onSuccess(result: Boolean) {
                        if (result) {
                            ToastUtil.toast("连接成功")
                            activity.findViewById<TextView>(R.id.address).text = socket!!.remoteDevice.address
                            activity.findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawers()
                        } else {
                            ToastUtil.toast("连接失败")
                        }
                    }
                })
                connectAsyncTask!!.execute()
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
    }
}