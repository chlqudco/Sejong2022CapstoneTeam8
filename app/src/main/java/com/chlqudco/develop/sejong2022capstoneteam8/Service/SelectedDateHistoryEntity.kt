package com.chlqudco.develop.sejong2022capstoneteam8.Service

import com.google.gson.annotations.SerializedName

data class SelectedDateHistoryEntity(
    @SerializedName("pushUpSet") val pushUpSet: Int,
    @SerializedName("pushUpCount") val pushUpCount: Int,
    @SerializedName("pullUpSet") val pullUpSet: Int,
    @SerializedName("pullUpCount") val pullUpCount: Int,
    @SerializedName("squatSet") val squatSet: Int,
    @SerializedName("squatCount") val squatCount: Int,
    @SerializedName("lungeSet") val lungeSet: Int,
    @SerializedName("lungeCount") val lungeCount: Int,
    @SerializedName("isTokenExpired") val isTokenExpired: Boolean,
    @SerializedName("isSuccessed") val isSuccessed: Boolean,
    @SerializedName("token") val token: String
)