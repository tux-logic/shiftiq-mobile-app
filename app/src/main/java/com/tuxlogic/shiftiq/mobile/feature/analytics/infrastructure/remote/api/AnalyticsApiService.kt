package com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.api

import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.BranchDailySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.BranchDailySummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.NetworkSummaryResourceDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnalyticsApiService {

    @GET("api/v1/analytics/branches/{branchId}/summary")
    suspend fun getBranchDailySummary(
        @Path("branchId") branchId: String
    ): Response<BranchDailySummaryResourceDto>

    @GET("api/v1/analytics/branches/{branchId}/financial")
    suspend fun getBranchFinancialAnalytics(
        @Path("branchId") branchId: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<BranchDailySnapshotResourceDto>>

    @GET("api/v1/analytics/network/summary")
    suspend fun getNetworkSummary(): Response<NetworkSummaryResourceDto>
}
