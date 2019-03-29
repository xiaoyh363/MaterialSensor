package com.xiaoyh.sensor.listener

import android.bluetooth.BluetoothSocket
import android.hardware.SensorManager
import android.widget.CompoundButton
import com.xiaoyh.sensor.MainActivity.Companion.accs
import com.xiaoyh.sensor.MainActivity.Companion.gyrs
import com.xiaoyh.sensor.MainActivity.Companion.mags
import com.xiaoyh.sensor.MainActivity.Companion.sensors
import com.xiaoyh.sensor.MainActivity.Companion.sm
import com.xiaoyh.sensor.util.LogUtil
import com.xiaoyh.sensor.util.ToastUtil
import com.xiaoyh.sensor.util.ConvertUtil

// 管理传感器以及发送蓝牙数据的开关
class MySwitchListener(
    private val socket: BluetoothSocket?,
    private val sensorListener: MySensorListener
) : CompoundButton.OnCheckedChangeListener {

    // 终止线程用变量
    @Volatile
    private var running = false

    // 定义蓝牙传输序列帧
    private val params = ByteArray(39) { i ->
        when (i) {
            0 -> 0x16
            1 -> 0x09
            2 -> 0x37
            else -> 0x00
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            running = true
            for (i in sensors) {
                sm.registerListener(
                    sensorListener,
                    sm.getDefaultSensor(i),
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
            LogUtil.d("开")
            socket?.let {
                val tempOut = it.outputStream
                Thread {
                    while (running) {
                        if (it.isConnected) {
                            bytes2params()
                            tempOut.write(params)
                            Thread.sleep(996)
                        } else {
                            break
                        }
                    }
                }.start()
            }
            socket ?: ToastUtil.toast("蓝牙未连接")
        } else {
            running = false
            sm.unregisterListener(sensorListener)
            LogUtil.d("关")
        }
    }

    private fun bytes2params() {
        val arrays = accs + mags + gyrs
        // accx 03-06 accy 07-10 accz 11-14
        // magx 15-18 magy 19-22 magz 23-26
        // gyrx 27-30 gyry 31-34 gyrz 35-38
        // i : 0-8
        // a : Float
        for ((i, a) in arrays.withIndex()) {
            val bytes = ConvertUtil.float2bytes(a)
            if (i == 0) {
                // 抽样检查 : accx
                LogUtil.d(ConvertUtil.bytes2float(bytes).toString())
            }
            for (j in 0..3) {
                params[3 + 4 * i + j] = bytes[j]
            }
        }
        //Log.d(tag, bytes2float(float2bytes(17.625F)).toString())
    }
}