package com.chlqudco.develop.sejong2022capstoneteam8.Service

import com.google.gson.annotations.SerializedName

data class  LoginUserEntity(
    @SerializedName("isSuccessed") val isSuccessed : Boolean,
    @SerializedName("name") val name : String,
    @SerializedName("token") val token : String
)
