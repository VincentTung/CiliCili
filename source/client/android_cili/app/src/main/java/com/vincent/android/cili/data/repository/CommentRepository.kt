package com.vincent.android.cili.data.repository

import com.vincent.android.cili.entity.CommentRequest
import com.vincent.android.cili.entity.CommentResponse
import com.vincent.android.cili.entity.ApiResult

interface CommentRepository {
    suspend fun addComment(request: CommentRequest): ApiResult<String>
    suspend fun getComments(videoId: Int, limit: Int = 10, offset: Int = 0): ApiResult<List<CommentResponse>>
    suspend fun getReplies(parentId: Long): ApiResult<List<CommentResponse>>
    suspend fun getCommentCount(videoId: Int): ApiResult<Int>
} 