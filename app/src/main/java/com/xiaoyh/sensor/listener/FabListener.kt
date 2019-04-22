package com.xiaoyh.sensor.listener

import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.xiaoyh.sensor.MainActivity
import com.xiaoyh.sensor.util.ConvertUtil
import com.xiaoyh.sensor.util.LogUtil
import com.xiaoyh.sensor.util.ToastUtil

class FabListener(
    private val fab: FloatingActionButton,
    private val sensorListener: MySensorListener
) : View.OnClickListener {

    companion object {
        val accs = FloatArray(3)
        val mags = FloatArray(3)
        val gyrs = FloatArray(3)
    }

    // 待用传感器数组：加速度计、磁力计、陀螺仪
    private val sensors = intArrayOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_GYROSCOPE)

    // 定义蓝牙传输序列帧
    private val params = ByteArray(39) { i ->
        when (i) {
            0 -> 0x16
            1 -> 0x09
            2 -> 0x37
            else -> 0x00
        }
    }

    // 终止线程用变量
    @Volatile
    var running = false

    override fun onClick(v: View?) {
        running = !running
        if (running) {
            fab.setImageResource(android.R.drawable.ic_media_pause)
            for (i in sensors) {
                MainActivity.sm.registerListener(
                    sensorListener,
                    MainActivity.sm.getDefaultSensor(i),
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
            MainActivity.socket?.let {
                val tempOut = it.outputStream
                Thread {
                    while (running) {
                        if (it.isConnected) {
                            bytes2params()
                            tempOut.write(params)
                            // 绘制折线图（每秒）
                            MainActivity.accLineChart.addData(accs)
                            MainActivity.magLineChart.addData(mags)
                            MainActivity.gyrLineChart.addData(gyrs)
                            Thread.sleep(970)
                        } else {
                            break
                        }
                    }
                }.start()
            }
            MainActivity.socket ?: ToastUtil.toast("蓝牙未连接")
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play)
            MainActivity.sm.unregisterListener(sensorListener)
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
        // LogUtil.d(ConvertUtil.bytes2float(ConvertUtil.float2bytes(17.625F)).toString())
    }
}
