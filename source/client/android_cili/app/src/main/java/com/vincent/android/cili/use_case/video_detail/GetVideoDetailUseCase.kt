package com.vincent.android.cili.use_case.video_detail

import com.vincent.android.cili.config.ERROR_CODE_UNKNOWN
import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.ApiResult
import com.vincent.android.cili.entity.VideoDetailEntity
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.logE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject


class GetVideoDetailUseCase @Inject constructor(private val mVideoRepository: VideoRepository) {

    operator fun invoke(id: Int): Flow<ApiResult<VideoDetailEntity>> = flow {
        log("开始获取视频详情，ID: $id")
        try {
            val result = mVideoRepository.getVideoDetail(id)
            log("Repository 返回结果: $result")
            emit(result)
        } catch (e: HttpException) {
            logE("GetVideoDetailUseCase","HTTP异常: ${e.message}", )
            emit(ApiResult<VideoDetailEntity>(e.localizedMessage ?: "网络请求失败", e.code()))
        } catch (e: Exception) {
            logE("GetVideoDetailUseCase","未知异常: ${e.message}")
            emit(ApiResult<VideoDetailEntity>(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN))
        }
    }
} 