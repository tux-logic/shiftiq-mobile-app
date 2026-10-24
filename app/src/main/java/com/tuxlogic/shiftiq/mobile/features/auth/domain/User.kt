package com.tuxlogic.shiftiq.mobile.features.auth.domain

data class User(
    val id: String,
    val email: String,
    val role: String,
    val token: String = "",
    val refreshToken: String = ""
)
