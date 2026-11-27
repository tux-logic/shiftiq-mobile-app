package com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetEmployeeAnalyticsSummaryUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(): AppResult<EmployeeAnalyticsSummary> =
        repository.getEmployeeAnalyticsSummary()
}
