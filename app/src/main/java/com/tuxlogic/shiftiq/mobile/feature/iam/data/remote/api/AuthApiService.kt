package com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.AuthenticatedUserResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.TokenResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: SignInRequestDto
    ): Response<AuthenticatedUserResponseDto>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequestDto
    ): Response<TokenResponseDto>

    @HTTP(method = "POST", path = "api/v1/auth/logout", hasBody = true)
    suspend fun logout(
        @Body request: RefreshTokenRequestDto
    ): Response<Unit>
}
