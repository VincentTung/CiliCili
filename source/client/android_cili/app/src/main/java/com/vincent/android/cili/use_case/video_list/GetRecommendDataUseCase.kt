package com.vincent.android.cili.use_case.video_list

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.VideoListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecommendDataUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(
        pageIndex: Int = 1,
        pageSize: Int = 10
    ): Flow<VideoListEntity> = flow {
        emit(videoRepository.getRecommendData(pageIndex, pageSize))
    }
} 