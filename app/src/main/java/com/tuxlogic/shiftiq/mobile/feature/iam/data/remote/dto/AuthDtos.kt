package com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName(value = "email", alternate = ["username"])
    val email: String,
    @SerializedName("password")
    val password: String
)

data class GoogleSignInRequestDto(
    @SerializedName("idToken")
    val idToken: String
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
    val password: String
)

data class UserResourceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName(value = "role", alternate = ["roles"])
    val role: String? = null
)

data class SendOtpRequestDto(
    @SerializedName("email")
    val email: String
)

data class SendOtpResponseDto(
    @SerializedName("message")
    val message: String,
    @SerializedName("expiresInSeconds")
    val expiresInSeconds: Long? = null
)

data class VerifyOtpRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)

data class VerifyOtpResponseDto(
    @SerializedName("verified")
    val verified: Boolean
)

data class PasswordRecoveryRequestDto(
    @SerializedName("email")
    val email: String
)

data class PasswordResetRequestDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("newPassword")
    val newPassword: String
)

data class UpdateEmailRequestDto(
    @SerializedName("email")
    val email: String
)

data class UpdatePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)

data class MessageResponseDto(
    @SerializedName("message")
    val message: String
)
