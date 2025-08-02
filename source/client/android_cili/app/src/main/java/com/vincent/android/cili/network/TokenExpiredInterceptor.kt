package com.vincent.android.cili.network

import android.content.Context
import com.vincent.android.cili.extensions.logW
import com.vincent.android.cili.util.LoginStateManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 令牌过期拦截器
 * 检测服务器返回的令牌过期错误，自动退出登录并跳转到登录界面
 */
class TokenExpiredInterceptor(private val context: Context) : Interceptor {
    
    companion object {
        private const val TAG = "TokenExpiredInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // 只检查HTTP状态码，避免消耗响应体
        if (response.code == 401) {
            logW(TAG, "Token expired detected in response code: 401")
            
            // 使用 LoginStateManager 处理令牌过期
            LoginStateManager.handleTokenExpired(context, "令牌已过期")
        }
        
        return response
    }
} 