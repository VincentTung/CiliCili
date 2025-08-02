package com.vincent.android.cili.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.vincent.android.cili.CiliApplication
import com.vincent.android.cili.entity.UserInfo
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.logE
import com.vincent.android.cili.extensions.logW
import com.vincent.android.cili.ui.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 登录状态管理器
 * 统一管理登录状态、令牌过期处理等
 */
object LoginStateManager {
    
    private const val TAG = "LoginStateManager"
    
    // 避免重复处理令牌过期
    private var isHandlingTokenExpired = false
    private var isLoggingOut = false // 添加退出登录状态标记
    
    @Volatile
    private var logoutInProgress = false // 使用 volatile 确保线程可见性
    
    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean {
        return AccountManager.userToken.isNotEmpty() && AccountManager.getUserInfo() != null
    }
    
    /**
     * 获取当前用户令牌
     */
    fun getCurrentToken(): String {
        return AccountManager.userToken
    }
    
    /**
     * 处理令牌过期
     * @param context 上下文
     * @param reason 过期原因（可选）
     */
    fun handleTokenExpired(context: Context, reason: String? = null) {
        if (isHandlingTokenExpired) {
            logW(TAG, "Already handling token expired, skipping...")
            return
        }
        
        isHandlingTokenExpired = true
        logW(TAG, "Handling token expired: $reason")
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 直接清除用户令牌，不调用 logout() 避免死循环
                AccountManager.userToken = ""
                
                // 清除其他相关数据
                clearUserData()
                
                // 显示提示信息
                showTokenExpiredMessage(context, reason)
                
                // 跳转到登录界面
                navigateToLogin(context)
                
            } catch (e: Exception) {
                logE(TAG, "Error handling token expired: ${e.message}")
            } finally {
                // 延迟重置状态，避免短时间内重复处理
                kotlinx.coroutines.delay(2000)
                isHandlingTokenExpired = false
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        // 使用双重检查锁定模式防止重复调用
        if (logoutInProgress) {
            log(TAG, "Logout already in progress, skipping...")
            return
        }
        
        synchronized(this) {
            if (logoutInProgress) {
                log(TAG, "Logout already in progress (double check), skipping...")
                return
            }
            logoutInProgress = true
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                log(TAG, "Logging out user...")
                
                // 清除用户令牌
                AccountManager.userToken = ""
                
                // 清除其他相关数据
                clearUserData()
                
                log(TAG, "User logged out successfully")
                
            } catch (e: Exception) {
                logE(TAG, "Error during logout: ${e.message}")
            } finally {
                // 延迟重置状态，避免短时间内重复处理
                kotlinx.coroutines.delay(2000)
                logoutInProgress = false
            }
        }
    }
    
    /**
     * 清除用户相关数据
     */
    private fun clearUserData() {
        try {

            CacheManager.clearInternalCache(CiliApplication.instance)
            
        } catch (e: Exception) {
            logE(TAG, "Error clearing user data: ${e.message}")
        }
    }
    
    /**
     * 显示令牌过期提示
     */
    private suspend fun showTokenExpiredMessage(context: Context, reason: String?) {
        withContext(Dispatchers.Main) {
            try {
                val message = reason ?: "登录已过期，请重新登录"
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                logE(TAG, "Failed to show toast: ${e.message}")
            }
        }
    }
    
    /**
     * 跳转到登录界面
     */
    private suspend fun navigateToLogin(context: Context) {
        withContext(Dispatchers.Main) {
            try {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                logE(TAG, "Failed to navigate to login: ${e.message}")
            }
        }
    }
    
    /**
     * 检查响应是否表示令牌过期
     */
    fun isTokenExpiredResponse(responseCode: Int, responseBody: String?): Boolean {
        // 检查HTTP状态码
        if (responseCode == 401) {
            return true
        }
        
        // 检查响应体内容
        responseBody?.let { body ->
            return try {
                val jsonObject = JSONObject(body)
                val message = jsonObject.optString("msg", "").lowercase()
                val code = jsonObject.optInt("code", 0)
                
                // 检查错误码
                if (code == 401) {
                    return true
                }
                
                // 检查错误消息
                message.contains("令牌过期") ||
                message.contains("token expired") ||
                message.contains("token已过期") ||
                message.contains("unauthorized") ||
                message.contains("invalid token") ||
                message.contains("token无效") ||
                message.contains("登录已过期")
                
            } catch (e: Exception) {
                logW(TAG, "Failed to parse JSON: ${e.message}")
                false
            }
        }
        
        return false
    }
} 