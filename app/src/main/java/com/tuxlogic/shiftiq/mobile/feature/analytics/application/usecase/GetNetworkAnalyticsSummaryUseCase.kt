package com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetNetworkAnalyticsSummaryUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(): AppResult<NetworkAnalyticsSummary> {
        return repository.getNetworkAnalyticsSummary()
    }
}
