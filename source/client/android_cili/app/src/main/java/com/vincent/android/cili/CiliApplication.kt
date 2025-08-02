package com.vincent.android.cili
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide
import com.vincent.android.cili.network.ApiHelper
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.util.MyCrashHandler
import com.vincent.android.cili.ui.activity.ThemeDataStore
import com.vincent.android.cili.ui.activity.ThemeMode
import com.vincent.android.cili.util.ProcessUtil.getRenderThreadTid
import com.vincent.ble.led.LEDController
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates



@HiltAndroidApp
class CiliApplication : Application(), ViewModelStoreOwner {

    companion object {
        var instance: CiliApplication by Delegates.notNull()
        private const val TAG = "CiliApplication"
        private const val HIGH_PRIORITY = -19
    }

    override val viewModelStore: ViewModelStore by lazy { ViewModelStore() }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 调整主线程优先级 默认为0
        Process.setThreadPriority(HIGH_PRIORITY)
        // 调整渲染线程优先级,需要知道渲染线程的id
        getRenderThreadTid().takeIf { it != -1 }?.let { tid ->
            Process.setThreadPriority(tid, HIGH_PRIORITY)
        }
        // 自定义全局异常捕获处理
        Thread.setDefaultUncaughtExceptionHandler(MyCrashHandler())
    }

    override fun onCreate() {
        log(TAG, "onCreate")
        instance = this
        super.onCreate()
        init()


    }

    //主题设置
    private fun themeSet() {
        // 1. 先设置默认主题（如系统模式）
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // 2. 异步读取用户设置后再切换
        CoroutineScope(Dispatchers.Main).launch {
            val mode = ThemeDataStore.getThemeMode(this@CiliApplication)
            AppCompatDelegate.setDefaultNightMode(
                when (mode) {
                    ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
        }
    }

    fun init() {
        themeSet()
        LEDController.getInstance().initBLE(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        log(TAG," onTrimMemory(${level})")
        releaseModerate(level)
        releaseGlide(this, level)
        when (level) {
            TRIM_MEMORY_COMPLETE -> releaseAll(level)      // 应用在后台且内存极低
            TRIM_MEMORY_MODERATE -> releaseModerate(level) // 应用在后台
            TRIM_MEMORY_UI_HIDDEN -> releaseLight(level)   // UI不可见（如跳转其他Activity）
            else -> {}
        }
    }

    private fun releaseLight(level: Int) {
        // 仅释放UI相关资源
        releaseGlide(this, level)
        releaseGeneric(level)
    }

    private fun releaseModerate(level: Int) {
        releaseLight(level)
        //        releaseOkHttp()  // 保留活跃请求，取消排队请求
        //        MMKV.defaultMMKV().trim()
    }

    private fun releaseAll(level: Int) {
        releaseMMKV()
        ApiHelper.instance.cancelAllRequests()
        System.gc() // 最后触发GC（谨慎使用）
    }

    private fun releaseOkHttp() {
        // 清理OkHttp缓存（内存+磁盘）
        val okHttpClient = ApiHelper.instance.okHttpClient
        okHttpClient.dispatcher.executorService.shutdownNow() // 取消所有请求
        okHttpClient.connectionPool.evictAll()               // 清空连接池
        okHttpClient.cache?.delete()                         // 删除磁盘缓存（如果有）
    }

    private fun releaseGlide(context: Context, level: Int) {
        Glide.get(context).apply {
            clearMemory()      // 立即清空内存缓存
            trimMemory(level)   // 根据级别调整缓存（Glide已内置智能策略）
        }
    }

    private fun releaseMMKV() {
        // MMKV默认自动管理内存，极端情况下可：
        //        MMKV.defaultMMKV().trim()  // 整理内存碎片
        // 注意：不要调用clearAll()，会删除持久化数据！
    }

    private fun releaseGeneric(level: Int) {
        //根据level自定义app的内存释放的范围
        // 清理静态缓存（如单例中的集合）
        //        AppDataCache.clearTemporaryData()
        //
        //        // 释放WebView内存
        //        webView?.let {
        //            it.loadUrl("about:blank")
        //            it.clearHistory()
        //            it.destroy()
        //        }
    }
}