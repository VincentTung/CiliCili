package com.vincent.android.cili.study.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import java.io.BufferedReader
import java.io.FileReader
import java.util.regex.Pattern

object LowEndDeviceChecker {

    // 已知低端CPU型号关键词(部分示例，可根据需要扩展)
    private val LOW_END_CPU_KEYWORDS = listOf(
        "mt6735", "mt6737", "mt6580", "mt6572",  // 联发科低端
        "sc9830", "sc7731", "sc8830",            // 展讯低端
        "msm8916", "msm8909",                    // 高通4系列低端
        "exynos 3475", "exynos 7570",            // 三星低端
        "spreadtrum", "unisoc",                  // 展讯/紫光
        "kirin 650", "kirin 659",                // 麒麟低端
        "helio a22", "helio p10"                // 联发科低端
    )

    // 已知低端设备名称关键词(部分示例，可根据需要扩展)
    private val LOW_END_DEVICE_KEYWORDS = listOf(
        "redmi go", "redmi a", "redmi note 4g",
        "galaxy j", "galaxy grand", "galaxy core",
        "huawei y", "huawei enjoy", "honor play 3e",
        "realme c", "realme 2", "oppo a",
        "vivo y", "vivo u"
    )

    /**
     * 综合判断是否为低端设备
     */
    fun isLowEndDevice(context: Context): Boolean {
        return when {
            // 内存小于2GB
            getTotalMemory(context) < 2 * 1024 -> true

            // CPU核心数小于4
            getNumberOfCores() < 4 -> true

            // CPU型号匹配低端关键词
            isLowEndCpu() -> true

            // 设备名称匹配低端关键词
            isLowEndDeviceModel() -> true

            // Android Go版本设备
            isAndroidGoEdition(context) -> true

            // 其他情况不算低端
            else -> false
        }
    }

    /**
     * 获取设备总内存(单位MB)
     */
    private fun getTotalMemory(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem / (1024 * 1024)  // 转换为MB
    }

    /**
     * 获取CPU核心数
     */
    private fun getNumberOfCores(): Int {
        return try {
            // 获取CPU核心数的目录
            FileReader("/proc/cpuinfo").use { fileReader ->
                BufferedReader(fileReader).use { bufferedReader ->
                    var cores = 0
                    var line: String?
                    val pattern = Pattern.compile("processor\\s*:\\s*\\d+")
                    while (bufferedReader.readLine().also { line = it } != null) {
                        if (pattern.matcher(line!!).find()) {
                            cores++
                        }
                    }
                    cores.takeIf { it > 0 } ?: 1
                }
            }
        } catch (e: Exception) {
            // 出错时保守返回1
            1
        }
    }

    /**
     * 判断CPU是否为低端
     */
    private fun isLowEndCpu(): Boolean {
        val cpuInfo = Build.HARDWARE.toLowerCase() + " " + Build.BOARD.toLowerCase() + " " +
                Build.BRAND.toLowerCase() + " " + Build.SUPPORTED_ABIS.joinToString(" ").toLowerCase()

        return LOW_END_CPU_KEYWORDS.any { keyword ->
            cpuInfo.contains(keyword)
        }
    }

    /**
     * 判断设备型号是否为低端
     */
    private fun isLowEndDeviceModel(): Boolean {
        val model = Build.MODEL.toLowerCase() + " " + Build.DEVICE.toLowerCase() + " " +
                Build.PRODUCT.toLowerCase() + " " + Build.BRAND.toLowerCase()

        return LOW_END_DEVICE_KEYWORDS.any { keyword ->
            model.contains(keyword)
        }
    }

    /**
     * 判断是否为Android Go版本
     */
    private fun isAndroidGoEdition(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            pm.hasSystemFeature("com.android.feature.leanback_only") ||
            pm.hasSystemFeature("android.hardware.ram.low") ||
            Build.PRODUCT.toLowerCase().contains("go") ||
            Build.DEVICE.toLowerCase().contains("go")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取设备性能评分(0-100)
     */
    fun getDevicePerformanceScore(context: Context): Int {
        var score = 0

        // 内存评分(最大40分)
        val memoryGB = getTotalMemory(context) / 1024f
        score += when {
            memoryGB >= 6 -> 40
            memoryGB >= 4 -> 30
            memoryGB >= 3 -> 25
            memoryGB >= 2 -> 20
            else -> 10
        }

        // CPU核心数评分(最大30分)
        val cores = getNumberOfCores()
        score += when {
            cores >= 8 -> 30
            cores >= 6 -> 25
            cores >= 4 -> 20
            cores >= 2 -> 15
            else -> 10
        }

        // CPU型号评分(最大20分)
        score += if (isLowEndCpu()) 5 else 20

        // 设备型号评分(最大10分)
        score += if (isLowEndDeviceModel()) 2 else 10

        return score.coerceIn(0, 100)
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