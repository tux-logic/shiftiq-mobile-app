package com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetBranchDailySummaryUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(branchId: BranchId? = null): AppResult<BranchDailySummary> {
        return repository.getBranchDailySummary(branchId)
    }
}
