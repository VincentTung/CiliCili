package com.vincent.android.cili.entity

import com.squareup.moshi.JsonClass
import kotlin.random.Random

@JsonClass(generateAdapter = true)
data class ApiResult<T>(
    val msg: String?=null,
    val code: Int =0,
    val data: T? =null
){

    fun isSuccess():Boolean{
        return code == 200
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
       return Random.nextInt()
    }


}
