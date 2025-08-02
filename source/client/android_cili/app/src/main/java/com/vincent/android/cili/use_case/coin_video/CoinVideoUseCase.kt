package com.vincent.android.cili.use_case.coin_video

import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinVideoUseCase @Inject constructor(private val mVideoRepository: VideoRepository) {
    operator fun invoke(id:Int): Flow<ApiResult<String>> = flow{
        emit(mVideoRepository.coinVideo(id))
    }
}