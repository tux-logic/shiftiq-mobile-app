package com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursDay
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursException
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursExceptionInput
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursScope
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.OperatingHoursRepository
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.api.OperatingHoursApiService
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursDayDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursExceptionRequestDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OperatingHoursRequestDto
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OperatingHoursRepositoryImpl @Inject constructor(
    private val api: OperatingHoursApiService,
    private val dispatchers: DispatcherProvider
) : OperatingHoursRepository {
    override suspend fun getWeekly(scope: OperatingHoursScope, id: String): AppResult<List<OperatingHoursDay>> = withContext(dispatchers.io) {
        safeApiCall { if (scope == OperatingHoursScope.BRANCH) api.getBranchHours(id) else api.getWorkshopHours(id) }
            .map { values -> values.map(OperatingHoursDayDto::toDomain) }
    }

    override suspend fun updateWeekly(scope: OperatingHoursScope, id: String, days: List<OperatingHoursDay>): AppResult<List<OperatingHoursDay>> = withContext(dispatchers.io) {
        val request = OperatingHoursRequestDto(days.map { it.toDto() })
        safeApiCall { if (scope == OperatingHoursScope.BRANCH) api.updateBranchHours(id, request) else api.updateWorkshopHours(id, request) }
            .map { values -> values.map(OperatingHoursDayDto::toDomain) }
    }

    override suspend fun resetBranchHours(branchId: String): AppResult<Unit> = withContext(dispatchers.io) {
        safeApiCall { api.deleteBranchHours(branchId) }
    }

    override suspend fun getExceptions(scope: OperatingHoursScope, id: String): AppResult<List<OperatingHoursException>> = withContext(dispatchers.io) {
        safeApiCall { if (scope == OperatingHoursScope.BRANCH) api.getBranchExceptions(id) else api.getWorkshopExceptions(id) }
            .map { values -> values.map { it.toDomain() } }
    }

    override suspend fun createException(scope: OperatingHoursScope, id: String, input: OperatingHoursExceptionInput): AppResult<OperatingHoursException> = withContext(dispatchers.io) {
        val request = OperatingHoursExceptionRequestDto.from(input)
        safeApiCall { if (scope == OperatingHoursScope.BRANCH) api.createBranchException(id, request) else api.createWorkshopException(id, request) }
            .map { it.toDomain() }
    }

    override suspend fun deleteException(scope: OperatingHoursScope, id: String, exceptionId: String): AppResult<Unit> = withContext(dispatchers.io) {
        safeApiCall { if (scope == OperatingHoursScope.BRANCH) api.deleteBranchException(id, exceptionId) else api.deleteWorkshopException(id, exceptionId) }
    }

    private fun OperatingHoursDay.toDto() = OperatingHoursDayDto(
        dayOfWeek = dayOfWeek,
        open = open,
        openTime = if (open) openTime else null,
        closeTime = if (open) closeTime else null,
        slotDurationMinutes = slotDurationMinutes,
        source = source
    )
}
