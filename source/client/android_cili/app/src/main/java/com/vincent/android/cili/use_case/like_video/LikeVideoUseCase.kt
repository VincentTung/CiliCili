package com.vincent.android.cili.use_case.like_video

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LikeVideoUseCase @Inject constructor(private val mVideoRepository: VideoRepository) {

    operator fun invoke(id: Int, isLike: Boolean = true): Flow<ApiResult<String>> = flow {
        if (isLike) {
            emit(mVideoRepository.likeVideo(id))
        } else {
            emit(mVideoRepository.cancelLikeVideo(id))
        }
    }
}