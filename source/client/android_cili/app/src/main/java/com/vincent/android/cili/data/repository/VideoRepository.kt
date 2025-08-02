package com.vincent.android.cili.data.repository

import com.vincent.android.cili.config.DEFAULT_PAGE_SIZE
import com.vincent.android.cili.entity.*

interface VideoRepository {
    suspend fun getVideoList(
        category: String,
        pageIndex: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<VideoEntity>

    suspend fun getRecommendData(
        pageIndex: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): VideoListEntity

    suspend fun getRankList(
        sort: String,
        pageIndex: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<VideoEntity>
    
    suspend fun getVideoDetail(id: Int): ApiResult<VideoDetailEntity>
    suspend fun likeVideo(id: Int): ApiResult<String>
    suspend fun cancelLikeVideo(id: Int): ApiResult<String>
    suspend fun unlikeVideo(id: Int): ApiResult<String>
    suspend fun collectVideo(id: Int): ApiResult<String>
    suspend fun cancelCollectVideo(id: Int): ApiResult<String>
    suspend fun coinVideo(id: Int): ApiResult<String>

    /**
     *  focus uploader
     */
    suspend fun focusUpper(id: Int): ApiResult<String>
    suspend fun cancelFocusUpper(id: Int): ApiResult<String>

    suspend fun collectList(): ApiResult<CollectList>
    suspend fun fansList(): ApiResult<FansData>
    suspend fun coinRecord(): ApiResult<RankListEntity>
    suspend fun viewRecord(): ApiResult<RankListEntity>
    suspend fun likeRecord(): ApiResult<RankListEntity>
}