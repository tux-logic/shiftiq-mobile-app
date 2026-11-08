package com.tuxlogic.shiftiq.mobile.feature.analytics

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.AnalyticsSnapshotId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.repository.AnalyticsRepository
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetBranchDailySummaryUseCase
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetBranchFinancialAnalyticsUseCase
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetNetworkAnalyticsSummaryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AnalyticsUseCasesTest {

    private val repository: AnalyticsRepository = mockk()

    private lateinit var getBranchDailySummaryUseCase: GetBranchDailySummaryUseCase
    private lateinit var getBranchFinancialAnalyticsUseCase: GetBranchFinancialAnalyticsUseCase
    private lateinit var getNetworkAnalyticsSummaryUseCase: GetNetworkAnalyticsSummaryUseCase

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)

    private val sampleSummary = BranchDailySummary(
        branchId = branchId,
        totalRevenue = Money(1250.0),
        completedWorkOrdersCount = 4,
        totalAppointmentsCount = 7,
        lowStockAlertsCount = 2,
        dtcAlertsCount = 1
    )

    private val sampleSnapshot = BranchDailySnapshot(
        id = AnalyticsSnapshotId(UUID.randomUUID()),
        branchId = branchId,
        snapshotDate = "2026-10-07",
        totalRevenue = Money(950.0),
        completedWorkOrdersCount = 3,
        totalAppointmentsCount = 5,
        lowStockAlertsCount = 1,
        dtcAlertsCount = 0
    )

    private val sampleNetworkSummary = NetworkAnalyticsSummary(
        totalActiveBranchesCount = 3,
        totalNetworkRevenue = Money(4850.0),
        totalNetworkCompletedWorkOrders = 14,
        totalNetworkAppointments = 22,
        totalNetworkDtcAlerts = 3
    )

    @Before
    fun setUp() {
        getBranchDailySummaryUseCase = GetBranchDailySummaryUseCase(repository)
        getBranchFinancialAnalyticsUseCase = GetBranchFinancialAnalyticsUseCase(repository)
        getNetworkAnalyticsSummaryUseCase = GetNetworkAnalyticsSummaryUseCase(repository)
    }

    @Test
    fun `GetBranchDailySummaryUseCase delegates to repository`() = runTest {
        coEvery { repository.getBranchDailySummary(branchId) } returns AppResult.Success(sampleSummary)

        val result = getBranchDailySummaryUseCase(branchId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleSummary, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.getBranchDailySummary(branchId) }
    }

    @Test
    fun `GetBranchDailySummaryUseCase with null branchId delegates fallback to repository`() = runTest {
        coEvery { repository.getBranchDailySummary(null) } returns AppResult.Success(sampleSummary)

        val result = getBranchDailySummaryUseCase(null)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleSummary, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.getBranchDailySummary(null) }
    }

    @Test
    fun `GetBranchDailySummaryUseCase returns failure when repository fails`() = runTest {
        coEvery { repository.getBranchDailySummary(branchId) } returns AppResult.Failure(AppError.NotFound())

        val result = getBranchDailySummaryUseCase(branchId)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.NotFound)
    }

    @Test
    fun `GetBranchFinancialAnalyticsUseCase delegates to repository with dates`() = runTest {
        coEvery {
            repository.getBranchFinancialAnalytics(branchId, "2026-10-01", "2026-10-07")
        } returns AppResult.Success(listOf(sampleSnapshot))

        val result = getBranchFinancialAnalyticsUseCase(branchId, "2026-10-01", "2026-10-07")

        assertTrue(result is AppResult.Success)
        val list = (result as AppResult.Success).data
        assertEquals(1, list.size)
        assertEquals(sampleSnapshot, list[0])
        coVerify(exactly = 1) { repository.getBranchFinancialAnalytics(branchId, "2026-10-01", "2026-10-07") }
    }

    @Test
    fun `GetNetworkAnalyticsSummaryUseCase delegates to repository`() = runTest {
        coEvery { repository.getNetworkAnalyticsSummary() } returns AppResult.Success(sampleNetworkSummary)

        val result = getNetworkAnalyticsSummaryUseCase()

        assertTrue(result is AppResult.Success)
        assertEquals(sampleNetworkSummary, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.getNetworkAnalyticsSummary() }
    }

    @Test
    fun `GetNetworkAnalyticsSummaryUseCase returns forbidden failure when unauthorized`() = runTest {
        coEvery { repository.getNetworkAnalyticsSummary() } returns AppResult.Failure(AppError.Forbidden())

        val result = getNetworkAnalyticsSummaryUseCase()

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Forbidden)
    }
}
