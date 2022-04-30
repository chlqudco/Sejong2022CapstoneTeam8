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


    @POST("user/login/")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginUserEntity>

    @GET("post/all/")
    fun getAllPosts(): Call<SuccessEntity>

    @Multipart
    @POST("post/create/")
    fun uploadPost(
        @Part image : MultipartBody.Part,
        @Part ("content")requestBody : RequestBody
    ):Call<SuccessEntity>

}