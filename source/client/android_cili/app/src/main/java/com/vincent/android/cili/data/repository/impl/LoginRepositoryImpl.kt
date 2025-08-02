package com.vincent.android.cili.data.repository.impl

import android.content.Context
import com.vincent.android.cili.CiliApplication
import com.vincent.android.cili.data.api.VideoApi
import com.vincent.android.cili.data.repository.LoginRepository
import com.vincent.android.cili.entity.LoginResult
import com.vincent.android.cili.entity.UserInfo
import com.vincent.android.cili.entity.ApiResult
import com.vincent.android.cili.util.NetworkExceptionHandler
import com.vincent.android.cili.util.TokenExpiredHandler
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.logE

class LoginRepositoryImpl(private val videoApiService: VideoApi) : LoginRepository {
    
    private val context: Context = CiliApplication.instance
    private val TAG = "LoginRepositoryImpl"
    
    override suspend fun login(username: String, pwd: String): LoginResult {
        log(TAG, "开始登录请求: username=$username")
        
        return NetworkExceptionHandler.handleNetworkExceptionWithDefault(
            defaultResult = LoginResult("网络请求失败", 500, null, null),
            context = context
        ) {
            try {
                log(TAG, "发送登录请求到服务器")
                val result = videoApiService.login(username, pwd)
                log(TAG, "登录响应: $result")
                result
            } catch (e: Exception) {
                logE(TAG, "登录请求异常: ${e.message}", e)
                throw e
            }
        }
    }

    override suspend fun getUserInfo(): ApiResult<UserInfo> {
        log(TAG, "开始获取用户信息")
        
        return try {
            log(TAG, "发送用户信息请求到服务器")
            val result = videoApiService.userInfo()
            log(TAG, "用户信息响应: $result")
            result
        } catch (e: Exception) {
            logE(TAG, "获取用户信息异常: ${e.message}", e)
            ApiResult<UserInfo>(e.localizedMessage ?: "获取用户信息失败", 500)
        }
    }
}