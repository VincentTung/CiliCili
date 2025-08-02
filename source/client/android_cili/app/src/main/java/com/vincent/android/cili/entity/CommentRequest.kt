package com.vincent.android.cili.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentRequest(
    @Json(name = "videoId") val videoId: Int,
    @Json(name = "userId") val userId: Int,
    @Json(name = "username") val username: String,
    @Json(name = "content") val content: String,
    @Json(name = "parentId") val parentId: Long? = null
)

