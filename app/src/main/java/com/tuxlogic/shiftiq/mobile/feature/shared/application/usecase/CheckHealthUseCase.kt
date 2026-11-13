package com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.SystemHealth
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.HealthRepository
import javax.inject.Inject

class CheckHealthUseCase @Inject constructor(
    private val healthRepository: HealthRepository
) {
    suspend operator fun invoke(): AppResult<SystemHealth> {
        return healthRepository.getHealth()
    }

    suspend fun checkPing(): AppResult<SystemHealth> {
        return healthRepository.getRootPing()
    }
}
