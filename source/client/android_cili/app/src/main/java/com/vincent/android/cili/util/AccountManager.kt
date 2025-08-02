package com.vincent.android.cili.util

import android.annotation.SuppressLint
import com.vincent.android.cili.CiliApplication
import com.vincent.android.cili.entity.UserInfo

@SuppressLint("StaticFieldLeak")
object AccountManager {
    
    /**
     * 退出登录
     * 使用 LoginStateManager 进行统一管理
     */
    fun logout() {
        // 直接调用 LoginStateManager，避免循环调用
        LoginStateManager.logout()
    }

    /**
     * 设置用户信息
     */
    fun setUserInfo(info: UserInfo) {
        this.userInfo = info
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(): UserInfo? {
        return this.userInfo
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return LoginStateManager.isLoggedIn()
    }
    
    /**
     * 获取当前令牌
     */
    fun getCurrentToken(): String {
        return LoginStateManager.getCurrentToken()
    }

    private var userInfo: UserInfo? = null
    var userToken by SharedPref<String>(CiliApplication.instance, "token", "")
}