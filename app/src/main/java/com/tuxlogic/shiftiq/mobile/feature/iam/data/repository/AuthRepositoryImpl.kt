package com.tuxlogic.shiftiq.mobile.feature.iam.data.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.api.AuthApiService
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.RefreshTokenRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.SignInRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.UserResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<AuthenticatedUser> =
        withContext(dispatchers.io) {
            val result = safeApiCall {
                authApiService.login(SignInRequestDto(email = email, password = password))
            }
            result.map { dto ->
                val role = Role.fromName(dto.role) ?: Role.ROLE_USER
                val userId = UserId(dto.id)
                sessionDataStore.saveSession(
                    accessToken = dto.accessToken,
                    refreshToken = dto.refreshToken,
                    userId = userId,
                    email = dto.email,
                    role = role
                )
                AuthenticatedUser(
                    id = userId,
                    email = dto.email,
                    role = role,
                    accessToken = dto.accessToken,
                    refreshToken = dto.refreshToken
                )
            }
        }

    override suspend fun register(
        email: String,
        password: String,
        roles: List<String>
    ): AppResult<UserResourceDto> = withContext(dispatchers.io) {
        safeApiCall {
            authApiService.register(
                com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.SignUpRequestDto(
                    email = email,
                    password = password,
                    roles = roles
                )
            )
        }
    }

    override suspend fun logout(): AppResult<Unit> = withContext(dispatchers.io) {
        val session = sessionDataStore.sessionState.first()
        val refreshToken = session.refreshToken
        if (refreshToken != null) {
            safeApiCall {
                authApiService.logout(RefreshTokenRequestDto(refreshToken))
            }
        }
        sessionDataStore.clearSession()
        AppResult.Success(Unit)
    }

    override fun observeSession(): Flow<SessionState> = sessionDataStore.sessionState

    override suspend fun selectBranch(branchId: BranchId): AppResult<Unit> = withContext(dispatchers.io) {
        sessionDataStore.setActiveBranchId(branchId)
        AppResult.Success(Unit)
    }
}
