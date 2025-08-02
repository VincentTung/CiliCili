package com.vincent.android.cili.util

import android.content.Context
import com.vincent.android.cili.config.ERROR_CODE_UNKNOWN
import com.vincent.android.cili.config.ERROR_CODE_NETWORK_UNAVAILABLE
import com.vincent.android.cili.entity.ApiResult
import retrofit2.HttpException

/**
 * 网络异常处理工具类
 * 统一处理 NetworkUnavailable 和其他网络异常
 */
object NetworkExceptionHandler {
    
    /**
     * 处理网络异常，返回 ApiResult
     * @param context 上下文，用于令牌过期处理
     */
    suspend inline fun <T> handleNetworkException(
        context: Context? = null,
        crossinline block: suspend () -> T
    ): ApiResult<T> {
        return try {
            ApiResult(data = block())
        } catch (e: HttpException) {
            // 检查是否为令牌过期
            if (context != null && TokenExpiredHandler.isTokenExpiredResponse(e.code(), null)) {
                TokenExpiredHandler.handleTokenExpired(context, "令牌已过期")
            }
            ApiResult(e.localizedMessage ?: "网络请求失败", e.code())
        } catch (e: NetworkUnavailable) {
            ApiResult("网络连接不可用，请检查网络设置", ERROR_CODE_NETWORK_UNAVAILABLE)
        } catch (e: Exception) {
            ApiResult(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN)
        }
    }
    
    /**
     * 处理网络异常，返回指定类型的错误结果
     * @param context 上下文，用于令牌过期处理
     */
    suspend inline fun <T> handleNetworkExceptionWithDefault(
        defaultResult: T,
        context: Context? = null,
        crossinline block: suspend () -> T
    ): T {
        return try {
            block()
        } catch (e: HttpException) {
            // 检查是否为令牌过期
            if (context != null && TokenExpiredHandler.isTokenExpiredResponse(e.code(), null)) {
                TokenExpiredHandler.handleTokenExpired(context, "令牌已过期")
            }
            defaultResult
        } catch (e: NetworkUnavailable) {
            defaultResult
        } catch (e: Exception) {
            defaultResult
        }
    }
    
    /**
     * 处理网络异常，支持自定义令牌过期处理
     * @param context 上下文
     * @param onTokenExpired 令牌过期时的回调
     */
    suspend inline fun <T> handleNetworkExceptionWithTokenExpired(
        context: Context,
        crossinline onTokenExpired: () -> Unit = {},
        crossinline block: suspend () -> T
    ): ApiResult<T> {
        return try {
            ApiResult(data = block())
        } catch (e: HttpException) {
            // 检查是否为令牌过期
            if (TokenExpiredHandler.isTokenExpiredResponse(e.code(), null)) {
                onTokenExpired()
                TokenExpiredHandler.handleTokenExpired(context, "令牌已过期")
            }
            ApiResult(e.localizedMessage ?: "网络请求失败", e.code())
        } catch (e: NetworkUnavailable) {
            ApiResult("网络连接不可用，请检查网络设置", ERROR_CODE_NETWORK_UNAVAILABLE)
        } catch (e: Exception) {
            ApiResult(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN)
        }
    }
} 