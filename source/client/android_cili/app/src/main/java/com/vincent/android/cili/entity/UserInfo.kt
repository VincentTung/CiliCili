package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
import com.vincent.android.cili.annotation.NoArgAllOpen


// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class UserInfo(
    val name: String = "",
    val face: String = "",
    val fans: Int = 0,
    val collect: Int = 0,
    val like: Int = 0,
    val coin: Int = 0,
    var view: Int = 0,
    val bannerList: List<BannerData> = emptyList(),
    val courseList:List<String> = emptyList()
)