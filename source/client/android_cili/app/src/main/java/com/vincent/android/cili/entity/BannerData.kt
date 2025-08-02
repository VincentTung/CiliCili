package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BannerData
    (
    val id: Int = 0,
    val sticky: Int = 0,
    val type: String ="",
    val title: String = "",
    val subtitle: String = "",
    val url: String = "",
    val cover: String = "",
    val createTime: String = "",
)
