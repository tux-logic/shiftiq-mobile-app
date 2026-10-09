package com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = authRepository.logout()
}

class ObserveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<SessionState> = authRepository.observeSession()
}

class SelectBranchUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(branchId: BranchId): AppResult<Unit> =
        authRepository.selectBranch(branchId)
}
