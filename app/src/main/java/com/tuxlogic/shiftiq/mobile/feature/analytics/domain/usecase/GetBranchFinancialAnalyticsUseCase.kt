package com.tuxlogic.shiftiq.mobile.feature.analytics.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetBranchFinancialAnalyticsUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(
        branchId: BranchId? = null,
        startDate: String? = null,
        endDate: String? = null
    ): AppResult<List<BranchDailySnapshot>> {
        return repository.getBranchFinancialAnalytics(branchId, startDate, endDate)
    }
}
