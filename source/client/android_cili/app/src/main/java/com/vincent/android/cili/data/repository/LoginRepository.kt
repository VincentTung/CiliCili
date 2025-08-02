package com.vincent.android.cili.data.repository

import com.vincent.android.cili.entity.LoginResult
import com.vincent.android.cili.entity.UserInfo
import com.vincent.android.cili.entity.ApiResult

interface LoginRepository {
    suspend fun login(username:String,pwd:String): LoginResult
    suspend fun getUserInfo():ApiResult<UserInfo>
}