package com.vincent.android.cili.network

import com.vincent.android.cili.BuildConfig
import com.vincent.android.cili.CiliApplication
import com.vincent.android.cili.config.BASE_URL
import com.vincent.android.cili.data.api.VideoApi
import com.vincent.android.cili.datatraffic.OkHttpMonitorInterceptor
import com.vincent.android.cili.extensions.log

import com.vincent.android.cili.util.ConnectionManager
import com.vincent.android.cili.network.NetworkStatusInterceptor
import com.vincent.android.cili.network.TokenExpiredInterceptor
import okhttp3.Cache
import okhttp3.Call
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit


class ApiHelper private constructor() {
    private val calls = mutableSetOf<Call>()

    companion object {
        private const val TAG = "http"
        private const val OKHTTP_CACHE_SIZE = (10 * 1024 * 1024).toLong()
        val instance: ApiHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ApiHelper() }
    }


    private var mOkHttpClient: OkHttpClient
    private lateinit var mRetrofit: Retrofit

    private val mContext by lazy {
        CiliApplication.instance
    }

    private val mVideoService by lazy {
        mRetrofit.create(VideoApi::class.java)
    }

    val okHttpClient: OkHttpClient get() = mOkHttpClient
    val videoService: VideoApi get() = mVideoService

    init {
        val httpClientBuilder = OkHttpClient.Builder()
        
        // 添加令牌过期拦截器（优先级最高，最先执行）
        httpClientBuilder.addInterceptor(TokenExpiredInterceptor(mContext))
        
        httpClientBuilder.addInterceptor(ApiAuthInterceptor())
        //流量监控
        httpClientBuilder.addNetworkInterceptor(OkHttpMonitorInterceptor())
        httpClientBuilder.cache(
            Cache(
                mContext.getExternalFilesDir(null)!!,
                OKHTTP_CACHE_SIZE
            )
        )

        val connectionPool = ConnectionPool(
            maxIdleConnections = 10,  // 最大空闲连接数
            keepAliveDuration = 10,   // 存活时间（分钟）
            timeUnit = TimeUnit.MINUTES
        )
        httpClientBuilder.addInterceptor(NetworkStatusInterceptor(ConnectionManager(mContext)))
        if (BuildConfig.DEBUG) {
            val level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
            val loggingInterceptor = HttpLoggingInterceptor { message: String ->
                log(
                    TAG,
                    "======$message"
                )
            }
            loggingInterceptor.setLevel(level)
            httpClientBuilder.addInterceptor(loggingInterceptor)
            httpClientBuilder.addInterceptor { chain ->
                val call = chain.call()
                synchronized(calls) { calls.add(call) }
                chain.proceed(chain.request()).also {
                    synchronized(calls) { calls.remove(call) }
                }
            }
        }


        val executor: Executor = Executors.newCachedThreadPool(object : ThreadFactory {
            override fun newThread(r: Runnable?): Thread? {
                log(TAG, "new Thread for $r")
                return Thread(r)
            }
        })
        mOkHttpClient = httpClientBuilder.build()
        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
        
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(mOkHttpClient)
            .build()
    }

    //取消请求
    fun cancelAllRequests() {
        synchronized(calls) {
            calls.forEach { it.cancel() }
            calls.clear()
        }
    }


}