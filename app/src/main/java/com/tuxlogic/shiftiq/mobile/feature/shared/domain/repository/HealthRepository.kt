package com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.SystemHealth

interface HealthRepository {
    suspend fun getRootPing(): AppResult<SystemHealth>
    suspend fun getHealth(): AppResult<SystemHealth>
}
