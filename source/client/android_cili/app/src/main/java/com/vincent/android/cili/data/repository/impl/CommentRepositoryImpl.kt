package com.vincent.android.cili.data.repository.impl

import com.vincent.android.cili.config.ERROR_CODE_UNKNOWN
import com.vincent.android.cili.data.api.VideoApi
import com.vincent.android.cili.data.repository.CommentRepository
import com.vincent.android.cili.entity.CommentRequest
import com.vincent.android.cili.entity.CommentResponse
import com.vincent.android.cili.entity.ApiResult
import com.vincent.android.cili.util.NetworkUnavailable
import retrofit2.HttpException

class CommentRepositoryImpl(private val videoApi: VideoApi) : CommentRepository {
    override suspend fun addComment(request: CommentRequest): ApiResult<String> {
        return try {
            videoApi.addComment(request)
        } catch (e: HttpException) {
            ApiResult(e.localizedMessage ?: "网络请求失败", e.code())
        } catch (e: NetworkUnavailable) {
            ApiResult("网络连接不可用，请检查网络设置", 503)
        } catch (e: Exception) {
            ApiResult(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN)
        }
    }

    override suspend fun getComments(videoId: Int, limit: Int, offset: Int): ApiResult<List<CommentResponse>> {
        return try {
            videoApi.getComments(videoId, limit, offset)
        } catch (e: HttpException) {
            ApiResult(e.localizedMessage ?: "网络请求失败", e.code())
        } catch (e: NetworkUnavailable) {
            ApiResult("网络连接不可用，请检查网络设置", 503)
        } catch (e: Exception) {
            ApiResult(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN)
        }
    }

    override suspend fun getReplies(parentId: Long): ApiResult<List<CommentResponse>> {
        return try {
            videoApi.getReplies(parentId)
        } catch (e: HttpException) {
            ApiResult(e.localizedMessage ?: "网络请求失败", e.code())
        } catch (e: NetworkUnavailable) {
            ApiResult("网络连接不可用，请检查网络设置", 503)
        } catch (e: Exception) {
            ApiResult(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN)
        }
    }

    override suspend fun getCommentCount(videoId: Int): ApiResult<Int> {
        return try {
            videoApi.getCommentCount(videoId)
        } catch (e: HttpException) {
            ApiResult(e.localizedMessage ?: "网络请求失败", e.code())
        } catch (e: NetworkUnavailable) {
            ApiResult("网络连接不可用，请检查网络设置", 503)
        } catch (e: Exception) {
            ApiResult(e.localizedMessage ?: "未知错误", ERROR_CODE_UNKNOWN)
        }
    }
} 