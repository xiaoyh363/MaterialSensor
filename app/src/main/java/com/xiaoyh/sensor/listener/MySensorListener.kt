package com.xiaoyh.sensor.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.xiaoyh.sensor.MainActivity

class MySensorListener : SensorEventListener {

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                for (i in 0..2) {
                    MainActivity.accs[i] = event.values[i]
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                for (i in 0..2) {
                    MainActivity.mags[i] = event.values[i]
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                for (i in 0..2) {
                    MainActivity.gyrs[i] = event.values[i]
                }
            }
        }

        // 绘制折线图
        MainActivity.accLineChart.addData(MainActivity.accs)
        MainActivity.magLineChart.addData(MainActivity.mags)
        MainActivity.gyrLineChart.addData(MainActivity.gyrs)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}