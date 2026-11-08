package com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.shared.infrastructure.remote.api.HealthApiService
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.model.SystemHealth
import com.tuxlogic.shiftiq.mobile.feature.shared.domain.repository.HealthRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthApiService: HealthApiService,
    private val dispatchers: DispatcherProvider
) : HealthRepository {

    override suspend fun getRootPing(): AppResult<SystemHealth> = withContext(dispatchers.io) {
        safeApiCall { healthApiService.getRootPing() }
            .map { it.toDomain() }
    }

    override suspend fun getHealth(): AppResult<SystemHealth> = withContext(dispatchers.io) {
        safeApiCall { healthApiService.getHealth() }
            .map { it.toDomain() }
    }
}
