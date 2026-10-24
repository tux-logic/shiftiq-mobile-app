package com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote

import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.AuthenticatedUserDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.GoogleSignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.PasswordRecoveryRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.ResetPasswordRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.remote.dto.SignUpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("authentication/sessions")
    suspend fun signIn(@Body request: SignInRequestDto): Response<AuthenticatedUserDto>

    @POST("authentication/sessions/google")
    suspend fun googleSignIn(@Body request: GoogleSignInRequestDto): Response<AuthenticatedUserDto>

    @POST("users")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<AuthenticatedUserDto>

    @POST("authentication/password-recoveries")
    suspend fun requestPasswordRecovery(@Body request: PasswordRecoveryRequestDto): Response<Unit>

    @POST("authentication/password-resets")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>
}
