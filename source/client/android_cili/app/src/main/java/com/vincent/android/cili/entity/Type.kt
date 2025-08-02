package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Type(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val sort: Int = 0
) 