package com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.api.AnalyticsApiService
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsApiService: AnalyticsApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : AnalyticsRepository {

    override suspend fun getBranchDailySummary(branchId: BranchId?): AppResult<BranchDailySummary> =
        withContext(dispatchers.io) {
            val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
            if (effectiveBranchId == null) {
                return@withContext AppResult.Failure(
                    AppError.Validation(details = "branchId", message = "No active branch selected")
                )
            }
            safeApiCall {
                analyticsApiService.getBranchDailySummary(effectiveBranchId.toString())
            }.map { it.toDomain() }
        }

    override suspend fun getBranchFinancialAnalytics(
        branchId: BranchId?,
        startDate: String?,
        endDate: String?
    ): AppResult<List<BranchDailySnapshot>> = withContext(dispatchers.io) {
        val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
        if (effectiveBranchId == null) {
            return@withContext AppResult.Failure(
                AppError.Validation(details = "branchId", message = "No active branch selected")
            )
        }
        safeApiCall {
            analyticsApiService.getBranchFinancialAnalytics(
                branchId = effectiveBranchId.toString(),
                startDate = startDate,
                endDate = endDate
            )
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getNetworkAnalyticsSummary(): AppResult<NetworkAnalyticsSummary> =
        withContext(dispatchers.io) {
            safeApiCall {
                analyticsApiService.getNetworkSummary()
            }.map { it.toDomain() }
        }
}
