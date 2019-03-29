package com.xiaoyh.sensor.util

import android.widget.Toast

object ToastUtil {

    fun toast(msg: String) {
        val toast = Toast.makeText(ContextUtil.getContext(), "", Toast.LENGTH_SHORT)
        toast.setText(msg)
        toast.show()
    }
}