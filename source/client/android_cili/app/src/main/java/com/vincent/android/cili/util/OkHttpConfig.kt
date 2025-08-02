package com.vincent.android.cili.util

import android.content.Context
import com.vincent.android.cili.config.BASE_URL
import com.vincent.android.cili.extensions.log
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import android.util.Log
import com.vincent.android.cili.BuildConfig
import okhttp3.ConnectionPool
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * OkHttp 配置工具类
 * 提供统一的网络客户端配置
 */
object OkHttpConfig {
    
    private const val TAG = "OkHttpConfig"
    
    /**
     * 创建优化的 OkHttpClient
     * @param context 上下文
     * @param additionalInterceptors 额外的拦截器
     * @return 配置好的 OkHttpClient
     */
    fun createOkHttpClient(
        context: Context,
        additionalInterceptors: List<Interceptor> = emptyList()
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // 基础超时配置
            .connectTimeout(15, TimeUnit.SECONDS)    // 连接超时
            .writeTimeout(30, TimeUnit.SECONDS)      // 写入超时
            .readTimeout(30, TimeUnit.SECONDS)       // 读取超时
            .callTimeout(60, TimeUnit.SECONDS)       // 调用超时
            
            // 连接池配置
            .connectionPool(
                ConnectionPool(
                    maxIdleConnections = 30,     // 最大空闲连接数
                    keepAliveDuration = 5,       // 连接保持时间（分钟）
                    timeUnit = TimeUnit.MINUTES
                )
            )
            
            // 重试机制
            .retryOnConnectionFailure(true)
            
            // 添加日志拦截器（仅在调试模式）
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(createLoggingInterceptor())
                }
            }
            
            // 添加自定义拦截器
            .apply {
                additionalInterceptors.forEach { interceptor ->
                    addInterceptor(interceptor)
                }
            }
            
            .build()
    }
    
    /**
     * 创建日志拦截器
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            log(TAG, "OkHttp: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
            
            // 隐藏敏感信息
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("X-API-Key")
        }
    }
    
    /**
     * 创建 Referer 拦截器
     */
    fun createRefererInterceptor(referer: String): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Referer", referer)
                .build()
            chain.proceed(newRequest)
        }
    }
    
    /**
     * 创建用户代理拦截器
     */
    fun createApiAuthInterceptor(userAgent: String): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("User-Agent", userAgent)
                .build()
            chain.proceed(newRequest)
        }
    }
    
    /**
     * 创建缓存控制拦截器
     */
    fun createCacheControlInterceptor(maxAgeSeconds: Int): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            
            response.newBuilder()
                .header("Cache-Control", "max-age=$maxAgeSeconds")
                .build()
        }
    }
    
    /**
     * 创建网络状态拦截器
     */
    fun createNetworkStateInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            // 检查网络连接状态
            if (!isNetworkAvailable(context)) {
                throw IOException("No network connection available")
            }
            chain.proceed(chain.request())
        }
    }
    
    /**
     * 检查网络是否可用
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
} 