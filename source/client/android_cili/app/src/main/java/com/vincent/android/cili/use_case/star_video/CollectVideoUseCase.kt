package com.vincent.android.cili.use_case.star_video

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 收藏/取消收藏视频
 */
class StarVideoUseCase @Inject constructor(private val mVideoRepository: VideoRepository) {
    operator fun invoke(id: Int, isStar: Boolean = true): Flow<ApiResult<String>> = flow {
        if (isStar) {
            emit(mVideoRepository.collectVideo(id))
        } else {
            emit(mVideoRepository.cancelCollectVideo(id))
        }
    }
}