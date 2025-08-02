package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CollectList (val list:List<VideoEntity>)