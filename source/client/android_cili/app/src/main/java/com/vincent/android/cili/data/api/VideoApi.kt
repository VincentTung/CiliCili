package com.vincent.android.cili.data.api

import com.vincent.android.cili.entity.*
import retrofit2.http.*

interface VideoApi {

    //多url
    @GET
    suspend fun getData(@Url fullUrl: String, @Header("name") name: String): String


    @GET("video/list")
    suspend fun getVideoListByCategory(
        @Query("category") category: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResult<VideoListEntity>


    @GET("video/rank")
    suspend fun getRankVideoList(
        @Query("sort") sort: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResult<RankListEntity>


    @GET("video/detail")
    suspend fun getVideoDetail(@Query("id") id: Int): ApiResult<VideoDetailEntity>


    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("pwd") pwd: String
    ): LoginResult

    @FormUrlEncoded
    @POST("action/like")
    suspend fun likeVideo(@Field("vid") vid: Int): ApiResult<String>

    @DELETE("action/like")
    suspend fun cancelLikeVideo(@Query("vid") vid: Int): ApiResult<String>


    @DELETE("action/hate")
    suspend fun unlikeVideo(@Query("vid") vid: Int): ApiResult<String>

    @FormUrlEncoded
    @POST("action/collect")
    suspend fun starVideo(@Field("vid") vid: Int): ApiResult<String>

    @DELETE("action/collect")
    suspend fun cancelStartVideo(@Query("vid") vid: Int): ApiResult<String>

    @FormUrlEncoded
    @POST("action/coin")
    suspend fun coinVideo(@Field("vid") vid: Int): ApiResult<String>


    @FormUrlEncoded
    @POST("action/focus")
    suspend fun focusUpper(@Field("uper") uperId: Int): ApiResult<String>


    @DELETE("action/focus")
    suspend fun cancelFocusUpper(@Query("uper") uperId: Int): ApiResult<String>

    @GET("action/fans")
    suspend fun fansList(): ApiResult<FansData>

    @GET("action/collect")
    suspend fun collectList(): ApiResult<CollectList>

    @GET("user/profile")
    suspend fun userInfo(): ApiResult<UserInfo>

    @GET("action/coin")
    suspend fun coinRecord(): ApiResult<RankListEntity>

    @GET("action/like")
    suspend fun likeRecord(): ApiResult<RankListEntity>

    @GET("action/view")
    suspend fun viewRecord(): ApiResult<RankListEntity>


    @GET("action/focus")
    suspend fun focusList(): ApiResult<FansData>


    @POST("comment/add")
    suspend fun addComment(@Body comment: CommentRequest): ApiResult<String>

    @GET("comment/list")
    suspend fun getComments(
        @Query("videoId") videoId: Int,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): ApiResult<List<CommentResponse>>

    @GET("comment/replies")
    suspend fun getReplies(
        @Query("parentId") parentId: Long
    ): ApiResult<List<CommentResponse>>

    @GET("comment/count")
    suspend fun getCommentCount(@Query("videoId") videoId: Int): ApiResult<Int>
}