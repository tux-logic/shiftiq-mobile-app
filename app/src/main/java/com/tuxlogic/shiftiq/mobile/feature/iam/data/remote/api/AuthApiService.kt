package com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.AuthenticatedUserResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.SignUpRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.TokenResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.UserResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/authentication/sessions")
    suspend fun login(
        @Body request: SignInRequestDto
    ): Response<AuthenticatedUserResponseDto>

    @POST("api/v1/authentication/sessions/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequestDto
    ): Response<TokenResponseDto>

    @HTTP(method = "DELETE", path = "api/v1/authentication/sessions", hasBody = true)
    suspend fun logout(
        @Body request: RefreshTokenRequestDto
    ): Response<Unit>

    @POST("api/v1/users")
    suspend fun register(
        @Body request: SignUpRequestDto
    ): Response<UserResourceDto>
}
