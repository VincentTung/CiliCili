package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
// import com.vincent.android.cili.annotation.NoArgAllOpen

// @NoArgAllOpen
@JsonClass(generateAdapter = true)
data class LoginResult( val msg:String? = null,val code:Int = 0,val token:String? = null,val uid:String? = null){

    fun isSuccess():Boolean = code ==200
}