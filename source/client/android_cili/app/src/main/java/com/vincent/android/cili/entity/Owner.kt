package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
// import com.vincent.android.cili.annotation.NoArgAllOpen

// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class Owner(
    val name: String = "",
    val face: String = "",
    val fans: Int = 0,
    val id: Int = 0,
    val isFocus: Boolean = false
)
