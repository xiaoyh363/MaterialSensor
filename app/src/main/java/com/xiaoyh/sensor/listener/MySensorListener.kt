package com.xiaoyh.sensor.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import com.xiaoyh.sensor.MainActivity.Companion.accs
import com.xiaoyh.sensor.MainActivity.Companion.gyrs
import com.xiaoyh.sensor.MainActivity.Companion.mags
import com.xiaoyh.sensor.MainActivity.Companion.acclineChart
import com.xiaoyh.sensor.R
import com.xiaoyh.sensor.util.ContextUtil

class MySensorListener(
    private val txv1: TextView,
    private val txv2: TextView,
    private val txv3: TextView
) : SensorEventListener {

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                for (i in 0..2) {
                    accs[i] = event.values[i]
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                for (i in 0..2) {
                    mags[i] = event.values[i]
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                for (i in 0..2) {
                    gyrs[i] = event.values[i]
                }
            }
        }
        txv1.text = String.format(ContextUtil.getString(R.string.txv), accs[0], accs[1], accs[2])
        txv2.text = String.format(ContextUtil.getString(R.string.txv), mags[0], mags[1], mags[2])
        txv3.text = String.format(ContextUtil.getString(R.string.txv), gyrs[0], gyrs[1], gyrs[2])

        acclineChart.addData(accs)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}