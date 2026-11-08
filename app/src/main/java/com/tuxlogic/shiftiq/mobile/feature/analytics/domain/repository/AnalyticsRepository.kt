package com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary

interface AnalyticsRepository {

    suspend fun getBranchDailySummary(
        branchId: BranchId? = null
    ): AppResult<BranchDailySummary>

    suspend fun getBranchFinancialAnalytics(
        branchId: BranchId? = null,
        startDate: String? = null,
        endDate: String? = null
    ): AppResult<List<BranchDailySnapshot>>

    suspend fun getNetworkAnalyticsSummary(): AppResult<NetworkAnalyticsSummary>
}
