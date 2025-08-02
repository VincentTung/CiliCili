package com.vincent.android.cili.entity

data class CommentResponse(
    val id: Long,
    val videoId: Int,
    val userId: Int,
    val username: String,
    val content: String,
    val avatar: String?,
    val createTime: String?,
    val parentId: Long?
)
