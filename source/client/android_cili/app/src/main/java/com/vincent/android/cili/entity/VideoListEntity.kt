package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
// import com.vincent.android.cili.annotation.NoArgAllOpen

// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class VideoListEntity(
    val videoList: List<VideoEntity> = emptyList(),
    val bannerList: List<BannerData> = emptyList(),
    val categoryList: List<Type> = emptyList()
)