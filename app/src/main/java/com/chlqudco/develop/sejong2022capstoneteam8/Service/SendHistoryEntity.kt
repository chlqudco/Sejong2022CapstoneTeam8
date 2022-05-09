package com.chlqudco.develop.sejong2022capstoneteam8.Service

import com.google.gson.annotations.SerializedName

data class SendHistoryEntity(
    @SerializedName("isTokenExpired") val isTokenExpired : Boolean,
    @SerializedName("isSuccessed") val isSuccessed : Boolean,
    @SerializedName("token") val token : String
)
