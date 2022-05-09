package com.chlqudco.develop.sejong2022capstoneteam8.Service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


//당연히 수정 필요
interface RetrofitService {

    @POST("/join.php")
    @FormUrlEncoded
    fun register(
        @Field("id") id: String,
        @Field("pw") pw: String,
        @Field("name") name: String
    ): Call<SuccessEntity>


    @POST("/login.php")
    @FormUrlEncoded
    fun login(
        @Field("id") id: String,
        @Field("pw") pw: String
    ): Call<LoginUserEntity>

    @POST("/history_show.php")
    @FormUrlEncoded
    fun getSelectedDateHistory(
        @Field("token") token: String,
        @Field("date") date: String
    ): Call<SelectedDateHistoryEntity>

    @POST("/history_insert.php")
    @FormUrlEncoded
    fun sendHistory(
        @Field("token") token: String,
        @Field("date") date: String,
        @Field("pushUpSet") pushUpSet: Int,
        @Field("pushUpCount") pushUpCount: Int,
        @Field("pullUpSet") pullUpSet: Int,
        @Field("pullUpCount") pullUpCount: Int,
        @Field("squatSet") squatSet: Int,
        @Field("squatCount") squatCount: Int,
        @Field("lungeSet") lungeSet: Int,
        @Field("lungeCount") lungeCount: Int,
    ): Call<SendHistoryEntity>

    /*
    @Multipart
    @POST("post/create/")
    fun uploadPost(
        @Part image : MultipartBody.Part,
        @Part ("content")requestBody : RequestBody
    ):Call<SuccessEntity>
    */
}