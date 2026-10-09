package com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class RefreshTokenRequestDto(
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class AuthenticatedUserResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class TokenResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)
