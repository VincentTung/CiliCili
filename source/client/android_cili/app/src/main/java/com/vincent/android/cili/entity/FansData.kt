package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
// import com.vincent.android.cili.annotation.NoArgAllOpen

// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class FansData(
    val total: Int = 0,
    val list: List<Owner> = emptyList()
)