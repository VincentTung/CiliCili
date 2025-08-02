package com.vincent.android.cili.data.repository.impl

import android.content.Context
import com.vincent.android.cili.CiliApplication
import com.vincent.android.cili.config.ERROR_CODE_UNKNOWN
import com.vincent.android.cili.data.api.VideoApi
import com.vincent.android.cili.data.repository.VideoRepository
import com.vincent.android.cili.entity.*
import com.vincent.android.cili.util.NetworkExceptionHandler
import com.vincent.android.cili.util.TokenExpiredHandler
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.logE

class VideoRepositoryImpl(
    private val videoApi: VideoApi
) : VideoRepository {
    
    private val context: Context = CiliApplication.instance
    
    override suspend fun getVideoList(
        category: String,
        pageIndex: Int,
        pageSize: Int
    ): List<VideoEntity> {
        return NetworkExceptionHandler.handleNetworkExceptionWithDefault(
            defaultResult = emptyList(),
            context = context
        ) {
            val result = videoApi.getVideoListByCategory(category, pageIndex, pageSize)
            result.data?.videoList ?: emptyList()
        }
    }
    
    override suspend fun getRecommendData(
        pageIndex: Int,
        pageSize: Int
    ): VideoListEntity {
        return NetworkExceptionHandler.handleNetworkExceptionWithDefault(
            defaultResult = VideoListEntity(),
            context = context
        ) {
            val result = videoApi.getVideoListByCategory("推荐", pageIndex, pageSize)
            val data = result.data ?: VideoListEntity()
            data
        }
    }
    
    override suspend fun getRankList(
        sort: String,
        pageIndex: Int,
        pageSize: Int
    ): List<VideoEntity> {
        return NetworkExceptionHandler.handleNetworkExceptionWithDefault(
            defaultResult = emptyList(),
            context = context
        ) {
            val result = videoApi.getRankVideoList(sort, pageIndex, pageSize)
            result.data?.list ?: emptyList()
        }
    }
    
    override suspend fun getVideoDetail(id: Int): ApiResult<VideoDetailEntity> {
        return try {
            val result = videoApi.getVideoDetail(id)
            result
        } catch (e: Exception) {
            ApiResult<VideoDetailEntity>("获取视频详情失败", ERROR_CODE_UNKNOWN)
        }
    }
    
    override suspend fun likeVideo(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.likeVideo(id)
            result.data ?: ""
        }
    }
    
    override suspend fun cancelLikeVideo(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.cancelLikeVideo(id)
            result.data ?: ""
        }
    }
    
    override suspend fun unlikeVideo(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.unlikeVideo(id)
            result.data ?: ""
        }
    }
    
    override suspend fun collectVideo(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.starVideo(id)
            result.data ?: ""
        }
    }
    
    override suspend fun cancelCollectVideo(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.cancelStartVideo(id)
            result.data ?: ""
        }
    }
    
    override suspend fun coinVideo(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.coinVideo(id)
            result.data ?: ""
        }
    }
    
    override suspend fun focusUpper(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.focusUpper(id)
            result.data ?: ""
        }
    }
    
    override suspend fun cancelFocusUpper(id: Int): ApiResult<String> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.cancelFocusUpper(id)
            result.data ?: ""
        }
    }
    
    override suspend fun collectList(): ApiResult<CollectList> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.collectList()
            result.data ?: CollectList(emptyList())
        }
    }
    
    override suspend fun fansList(): ApiResult<FansData> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.fansList()
            result.data ?: FansData()
        }
    }
    
    override suspend fun coinRecord(): ApiResult<RankListEntity> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.coinRecord()
            result.data ?: RankListEntity(list = emptyList())
        }
    }
    
    override suspend fun viewRecord(): ApiResult<RankListEntity> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.viewRecord()
            result.data ?: RankListEntity(list = emptyList())
        }
    }
    
    override suspend fun likeRecord(): ApiResult<RankListEntity> {
        return NetworkExceptionHandler.handleNetworkException(context) {
            val result = videoApi.likeRecord()
            result.data ?: RankListEntity(list = emptyList())
        }
    }
}