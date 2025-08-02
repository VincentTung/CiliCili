package com.vincent.android.cili.use_case.collect_list

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.CollectList
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CollectListUseCase @Inject constructor(private val videoRepository: VideoRepository) {

    operator fun invoke(): Flow<ApiResult<CollectList>> = flow {
        emit(videoRepository.collectList())
    }
}