package com.vincent.android.cili.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.CommentRequest
import com.vincent.android.cili.entity.CommentResponse
import com.vincent.android.cili.entity.Resource
import com.vincent.android.cili.entity.ApiResult
import com.vincent.android.cili.use_case.comment_video.CommentVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentUseCase: CommentVideoUseCase
) : ViewModel() {
    private val _sendState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val sendState: StateFlow<Resource<Unit>> = _sendState.asStateFlow()

    private val _commentList = MutableStateFlow<Resource<List<CommentResponse>>>(Resource.Loading())
    val commentList: StateFlow<Resource<List<CommentResponse>>> = _commentList.asStateFlow()

    private val _replyList = MutableStateFlow<Resource<List<CommentResponse>>>(Resource.Loading())
    val replyList: StateFlow<Resource<List<CommentResponse>>> = _replyList.asStateFlow()

    private val _commentCount = MutableStateFlow<Resource<Int>>(Resource.Loading())
    val commentCount: StateFlow<Resource<Int>> = _commentCount.asStateFlow()

    fun sendComment(request: CommentRequest) {
        commentUseCase.addComment(request)
            .onEach { result ->

                _sendState.value = if (result.isSuccess()) Resource.Success(Unit) else Resource.Error(result.msg ?: "评论失败")
            }
            .launchIn(viewModelScope)
    }

    fun getComments(videoId: Int, limit: Int = 10, offset: Int = 0) {
        commentUseCase.getComments(videoId, limit, offset)
            .onEach { result ->
                _commentList.value = if (result.isSuccess()) Resource.Success(result.data ?: emptyList()) else Resource.Error(result.msg ?: "获取评论失败")
            }
            .launchIn(viewModelScope)
    }

    fun getReplies(parentId: Long) {
        commentUseCase.getReplies(parentId)
            .onEach { result ->
                _replyList.value = if (result.isSuccess()) Resource.Success(result.data ?: emptyList()) else Resource.Error(result.msg ?: "获取回复失败")
            }
            .launchIn(viewModelScope)
    }

    fun getCommentCount(videoId: Int) {
        commentUseCase.getCommentCount(videoId)
            .onEach { result ->
                _commentCount.value = if (result.isSuccess()) Resource.Success(result.data ?: 0) else Resource.Error(result.msg ?: "获取评论数失败")
            }
            .launchIn(viewModelScope)
    }
} 