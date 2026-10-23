package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto

data class SignInRequestDto(
    val email: String,
    val password: String
)

data class SignUpRequestDto(
    val email: String,
    val password: String
)

data class GoogleSignInRequestDto(
    val idToken: String
)

data class AuthenticatedUserDto(
    val id: String,
    val email: String,
    val role: String,
    val token: String,
    val refreshToken: String,
    val accessTokenExpiresInSeconds: Long
)
