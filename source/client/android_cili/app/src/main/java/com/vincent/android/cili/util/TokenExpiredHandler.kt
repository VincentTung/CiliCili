package com.vincent.android.cili.util

import android.content.Context
import com.vincent.android.cili.extensions.logW

/**
 * 令牌过期处理工具类
 * 提供在其他地方手动处理令牌过期的功能
 */
object TokenExpiredHandler {
    
    private const val TAG = "TokenExpiredHandler"
    
    /**
     * 手动处理令牌过期
     * 可以在 ViewModel 或其他地方调用
     * @param context 上下文
     * @param reason 过期原因
     */
    fun handleTokenExpired(context: Context, reason: String? = null) {
        logW(TAG, "Manual token expired handling: $reason")
        LoginStateManager.handleTokenExpired(context, reason)
    }
    
    /**
     * 检查响应是否表示令牌过期
     * 可以在 Repository 或其他地方调用
     * @param responseCode HTTP响应码
     * @param responseBody 响应体
     * @return 是否表示令牌过期
     */
    fun isTokenExpiredResponse(responseCode: Int, responseBody: String?): Boolean {
        return LoginStateManager.isTokenExpiredResponse(responseCode, responseBody)
    }
    
    /**
     * 在 Repository 中处理令牌过期的示例
     * @param context 上下文
     * @param responseCode HTTP响应码
     * @param responseBody 响应体
     * @param errorMessage 错误消息
     */
    fun handleApiResponse(
        context: Context,
        responseCode: Int,
        responseBody: String?,
        errorMessage: String?
    ) {
        if (isTokenExpiredResponse(responseCode, responseBody)) {
            logW(TAG, "Token expired detected in API response: $errorMessage")
            handleTokenExpired(context, errorMessage ?: "令牌已过期")
        }
    }
} 