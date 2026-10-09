package com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName(value = "email", alternate = ["username"])
    val email: String,
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
    @SerializedName(value = "token", alternate = ["accessToken"])
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("accessTokenExpiresInSeconds")
    val accessTokenExpiresInSeconds: Long? = null
)

data class TokenResponseDto(
    @SerializedName(value = "token", alternate = ["accessToken"])
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class SignUpRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("roles")
    val roles: List<String> = listOf("ROLE_USER")
)

data class UserResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("roles")
    val roles: List<String> = emptyList()
)
