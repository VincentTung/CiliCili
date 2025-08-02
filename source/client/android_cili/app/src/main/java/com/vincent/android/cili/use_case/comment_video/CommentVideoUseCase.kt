package com.vincent.android.cili.use_case.comment_video

import com.vincent.android.cili.data.repository.CommentRepository
import com.vincent.android.cili.entity.CommentRequest
import com.vincent.android.cili.entity.CommentResponse
import com.vincent.android.cili.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommentVideoUseCase @Inject constructor(private val commentRepository: CommentRepository) {

    fun addComment(request: CommentRequest): Flow<ApiResult<String>> = flow {
        emit(commentRepository.addComment(request))
    }

    fun getComments(videoId: Int, limit: Int = 10, offset: Int = 0): Flow<ApiResult<List<CommentResponse>>> = flow {
        emit(commentRepository.getComments(videoId, limit, offset))
    }

    fun getReplies(parentId: Long): Flow<ApiResult<List<CommentResponse>>> = flow {
        emit(commentRepository.getReplies(parentId))
    }

    fun getCommentCount(videoId: Int): Flow<ApiResult<Int>> = flow {
        emit(commentRepository.getCommentCount(videoId))
    }
}