package com.vincent.android.cili.use_case.user_login

import com.vincent.android.cili.data.repository.LoginRepository
import com.vincent.android.cili.entity.LoginResult
import com.vincent.android.cili.util.HashUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserLoginUseCase @Inject constructor(private val loginRepository: LoginRepository) {
    operator fun invoke(username:String,pwd:String): Flow<LoginResult> = flow {
        emit(loginRepository.login(username, HashUtil.md5(pwd)?:""))
    }
}