package com.vincent.android.cili.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.bitmap.Downsampler
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.vincent.android.cili.BuildConfig
import com.vincent.android.cili.R
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.study.performance.LowEndDeviceChecker
import com.vincent.ble.base.logd
import com.vincent.android.cili.util.OkHttpConfig
import java.io.InputStream
import kotlin.apply
import kotlin.jvm.java

@GlideModule
class CliAppGlideModule : AppGlideModule() {


    companion object {
        private const  val TAG = "CliAppGlideModule"
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f) // 默认 2 屏
            .build()
        builder.setMemoryCache(LruResourceCache(calculator.getMemoryCacheSize().toLong()))

        log(TAG, "applyOptions")

        // 调试模式
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(Log.DEBUG)
        }

        when {
            LowEndDeviceChecker.isLowEndDevice(context) -> configureForLowEnd(builder)
            else -> configureForHighEnd(builder)
        }
    }

    // 低端机型配置
    private fun configureForLowEnd(builder: GlideBuilder) {
        log(TAG,"configureForLowEnd")
        builder.apply {
            // 内存缓存：8MB
            setMemoryCache(LruResourceCache(28 * 1024 * 1024))

            // 默认请求配置
            setDefaultRequestOptions(
                RequestOptions().disallowHardwareConfig()
                    .format(DecodeFormat.PREFER_RGB_565)  // 低内存格式
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // 只缓存原始数据
                    .downsample(DownsampleStrategy.AT_LEAST)   // 强制缩小图片
            )

            // 禁用动画解码
            setDefaultTransitionOptions(
                Drawable::class.java,
                DrawableTransitionOptions().dontTransition()
            )
        }
    }

    // 高端机型配置
    private fun configureForHighEnd(builder: GlideBuilder) {
        log(TAG,"configureForHighEnd")
        builder.apply {



            setMemoryCache(LruResourceCache(50 * 1024 * 1024))

            val requestOptions = RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .placeholder(R.drawable.ic_launcher_background) // 默认占位图
                .error(R.drawable.ic_launcher_foreground) // 默认错误图
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // 智能缓存策略
                .encodeQuality(85) // JPEG压缩质量（0-100）
                .timeout(15_000)//

            // 仅Android 8.0+启用硬件加速
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestOptions.set(
                    Downsampler.ALLOW_HARDWARE_CONFIG,
                    true
                )
            } else {
                requestOptions.disallowHardwareConfig()
            }

            setDefaultRequestOptions(requestOptions)
        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        log(TAG, "registerComponents")

        // 使用 OkHttpConfig 工具类创建优化的客户端
        val okHttpClient = OkHttpConfig.createOkHttpClient(
            context = context,
            additionalInterceptors = listOf(
                // 可以在这里添加额外的拦截器
                // OkHttpConfig.createRefererInterceptor("https://your-domain.com")
            )
        )
            
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )

        // 高低端机型共用组件注册
//        registry.append(ByteArray::class.java, Bitmap::class.java, ByteArrayBitmapDecoder())

        // 自定义WebP解码器
//        registry.append(
//            InputStream::class.java,
//            Drawable::class.java,
//            WebpDrawableDecoder(glide.bitmapPool)
//        )
    }

    // 禁用 Manifest 解析（提升性能）
    override fun isManifestParsingEnabled(): Boolean = false


}