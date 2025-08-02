package com.vincent.android.cili.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build

object DeviceUtils {
    /**
     * 判断设备是否为低内存设备
     * @return true: 低内存设备 false: 非低内存设备
     */
    fun isLowMemoryDevice(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // 方法1：使用官方API判断（最可靠）
        return activityManager.isLowRamDevice
    }

    /**
     * 兼容旧设备的启发式判断
     */
     fun isLowMemoryByHeuristic(context: Context): Boolean {

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // 获取内存信息
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // 判断条件（满足任一条件即视为低内存设备）
        return when {
            // 条件1：总内存 < 1.5GB
            memoryInfo.totalMem < 1024*6 * 1024 * 1024 -> true

            // 条件2：内存类 <= 64MB（通过反射获取，兼容旧设备）
            getHeapGrowthLimit() <= 64 -> true

            // 条件3：设备属于低端机型（通过型号判断）
            isLowEndDeviceModel() -> true

            else -> false
        }
    }

    /**
     * 通过设备型号匹配已知低端机型
     */
    private fun isLowEndDeviceModel(): Boolean {
        val lowEndModels = setOf(
            "Redmi Go", "Nokia 1", "Samsung Galaxy J1","Redmi Note 8",
            "Huawei Y3", "OPPO A1k", "vivo Y91"
        )
        return lowEndModels.any { model ->
            Build.MODEL.contains(model, ignoreCase = true)
        }
    }
    /**
     * 通过反射获取 dalvik.vm.heapgrowthlimit 值
     */
    private fun getHeapGrowthLimit(): Int {
        return try {
            val heapGrowthLimit = Class.forName("android.os.SystemProperties")
                .getMethod("get", String::class.java)
                .invoke(null, "dalvik.vm.heapgrowthlimit") as String
            heapGrowthLimit.replace("m", "").toInt()
        } catch (e: Exception) {
            256 // 默认返回较大值（避免误判）
        }
    }
}