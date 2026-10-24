package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val role: String,
    val token: String,
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String
)
