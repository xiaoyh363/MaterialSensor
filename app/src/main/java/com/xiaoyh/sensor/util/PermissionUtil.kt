package com.xiaoyh.sensor.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

object PermissionUtil {
    // 权限申请列表
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    fun getPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for ((i, p) in permissions.withIndex()) {
                if (activity.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(p), i)
                }
            }
        }
    }
}