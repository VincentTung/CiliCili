package com.vincent.android.cili.use_case.fans_list

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.FansData
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FansListUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    operator fun invoke(): Flow<ApiResult<FansData>> = flow {
        emit(videoRepository.fansList())
    }
}