package com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iam.data.remote.dto.UserResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<AuthenticatedUser>
    suspend fun register(email: String, password: String, roles: List<String> = listOf("ROLE_OWNER")): AppResult<UserResourceDto>
    suspend fun logout(): AppResult<Unit>
    fun observeSession(): Flow<SessionState>
    suspend fun selectBranch(branchId: BranchId): AppResult<Unit>
}
