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
                    FabListener.accs[i] = event.values[i]
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                for (i in 0..2) {
                    FabListener.mags[i] = event.values[i]
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                for (i in 0..2) {
                    FabListener.gyrs[i] = event.values[i]
                }
            }
        }

        // 绘制折线图（即时）
        /*MainActivity.accLineChart.addData(FabListener.accs)
        MainActivity.magLineChart.addData(FabListener.mags)
        MainActivity.gyrLineChart.addData(FabListener.gyrs)*/
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}