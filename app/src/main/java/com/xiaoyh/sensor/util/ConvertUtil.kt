package com.xiaoyh.sensor.util

object ConvertUtil {

    fun float2bytes(float: Float): ByteArray {
        val fblt = float.toBits()
        val bytes = ByteArray(4)
        for (i in 0..3) {
            bytes[i] = (fblt shr (24 - i * 8)).toByte()
        }
        return bytes
    }

    fun bytes2float(bytes: ByteArray): Float {
        var i: Int = bytes[3].toInt()
        i = i and 0xff
        i = i or (bytes[2].toInt() shl 8)
        i = i and 0xffff
        i = i or (bytes[1].toInt() shl 16)
        i = i and 0xffffff
        i = i or (bytes[0].toInt() shl 24)
        return Float.fromBits(i)
    }
}