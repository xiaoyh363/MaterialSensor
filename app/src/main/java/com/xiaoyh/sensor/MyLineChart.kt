package com.xiaoyh.sensor

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.xiaoyh.sensor.util.ContextUtil

class MyLineChart(private val chart: LineChart, msg: String) {

    private var x: Float = 0f
    private val lineDataMap = mapOf(
        "x" to getColor(R.color.md_red_300),
        "y" to getColor(R.color.md_green_300),
        "z" to getColor(R.color.md_blue_300)
    )
    private val lineDataSets = mutableListOf<LineDataSet>()

    private fun getColor(id: Int): Int {
        @Suppress("DEPRECATION")
        return ContextUtil.getContext().resources.getColor(id)
    }

    private fun initDataSets(lineDataMap: Map<String, Int>) {
        lineDataMap.forEach {
            val lineDataSet = LineDataSet(null, it.key)
            lineDataSet.color = it.value            // 设置折线颜色
            lineDataSet.lineWidth = 3f              // 设置折线宽度
            lineDataSet.circleRadius = 1.5f         // 设置圆点半径
            //lineDataSet.setDrawFilled(true)         // 填充折线图
            lineDataSet.highLightColor = it.value   // 设置高亮点颜色
            lineDataSet.setCircleColor(getColor(R.color.md_blue_100))
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER    // 圆滑折线
            lineDataSet.valueFormatter = object : ValueFormatter() {
                // 设置点值显示格式
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.2f", value)
                }
            }
            lineDataSet.valueTextSize = 8f  // 设置点值字体大小
            lineDataSets.add(lineDataSet)
        }
        chart.data = LineData(lineDataSets.toList())
    }

    init {
        initDataSets(lineDataMap)
        chart.xAxis.isEnabled = false       // 关闭横坐标
        //chart.axisLeft.granularity = 1f     // 设置左纵坐标最小间距为1.0
        chart.axisLeft.isEnabled = false    // 关闭左纵坐标
        chart.axisRight.isEnabled = false   // 关闭右纵坐标
        //chart.axisLeft.enableGridDashedLine(10f, 10f, 0f)   // 将格线设置为曲线
        // 设置折线图描述
        val description = Description()
        description.text = msg
        chart.description = description
        chart.invalidate()
    }

    private fun notifyDataSetChanged() {
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
        chart.setVisibleXRangeMaximum(10f)
        chart.moveViewToX(chart.data.entryCount - 10f)
    }

    fun addData(params: FloatArray) {
        for (i in 0 until params.size) {
            lineDataSets[i].addEntry(Entry(x, params[i]))
        }
        x++
        notifyDataSetChanged()
    }
}