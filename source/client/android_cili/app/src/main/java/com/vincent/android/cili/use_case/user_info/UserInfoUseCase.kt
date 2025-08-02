package com.vincent.android.cili.use_case.user_info
import com.vincent.android.cili.data.repository.LoginRepository
import com.vincent.android.cili.entity.ApiResult
import com.vincent.android.cili.entity.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserInfoUseCase @Inject constructor(private val loginRepository: LoginRepository) {
    operator fun invoke(): Flow<ApiResult<UserInfo>> = flow {
       emit( loginRepository.getUserInfo())
    }
}