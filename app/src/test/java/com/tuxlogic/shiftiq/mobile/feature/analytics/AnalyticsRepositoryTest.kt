package com.tuxlogic.shiftiq.mobile.feature.analytics

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.api.AnalyticsApiService
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.BranchDailySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.BranchDailySummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.NetworkSummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.repository.AnalyticsRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class AnalyticsRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val analyticsApiService: AnalyticsApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk()

    private lateinit var repository: AnalyticsRepositoryImpl

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)

    private val sampleSummaryDto = BranchDailySummaryResourceDto(
        branchId = branchUuid.toString(),
        totalRevenue = 1250.00,
        completedWorkOrdersCount = 4,
        totalAppointmentsCount = 7,
        lowStockAlertsCount = 2,
        dtcAlertsCount = 1
    )

    private val sampleSnapshotDto = BranchDailySnapshotResourceDto(
        id = UUID.randomUUID().toString(),
        branchId = branchUuid.toString(),
        snapshotDate = "2026-10-07",
        totalRevenue = 950.00,
        completedWorkOrdersCount = 3,
        totalAppointmentsCount = 5,
        lowStockAlertsCount = 1,
        dtcAlertsCount = 0
    )

    private val sampleNetworkSummaryDto = NetworkSummaryResourceDto(
        totalActiveBranchesCount = 3,
        totalNetworkRevenue = 4850.00,
        totalNetworkCompletedWorkOrders = 14,
        totalNetworkAppointments = 22,
        totalNetworkDtcAlerts = 3
    )

    @Before
    fun setUp() {
        repository = AnalyticsRepositoryImpl(analyticsApiService, sessionDataStore, dispatchers)
    }

    @Test
    fun `getBranchDailySummary with explicit branchId returns domain summary`() = runTest(testDispatcher) {
        coEvery { analyticsApiService.getBranchDailySummary(branchUuid.toString()) } returns Response.success(sampleSummaryDto)

        val result = repository.getBranchDailySummary(branchId)

        assertTrue(result is AppResult.Success)
        val summary = (result as AppResult.Success).data
        assertEquals(branchId, summary.branchId)
        assertEquals(Money(1250.00), summary.totalRevenue)
        assertEquals(4, summary.completedWorkOrdersCount)
        coVerify(exactly = 1) { analyticsApiService.getBranchDailySummary(branchUuid.toString()) }
    }

    @Test
    fun `getBranchDailySummary fallback to sessionDataStore when branchId is null`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery { analyticsApiService.getBranchDailySummary(branchUuid.toString()) } returns Response.success(sampleSummaryDto)

        val result = repository.getBranchDailySummary(null)

        assertTrue(result is AppResult.Success)
        assertEquals(branchId, (result as AppResult.Success).data.branchId)
        coVerify(exactly = 1) { analyticsApiService.getBranchDailySummary(branchUuid.toString()) }
    }

    @Test
    fun `getBranchDailySummary returns failure when branchId is null and session has no branch`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = repository.getBranchDailySummary(null)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { analyticsApiService.getBranchDailySummary(any()) }
    }

    @Test
    fun `getBranchFinancialAnalytics with explicit branchId returns snapshots list`() = runTest(testDispatcher) {
        coEvery {
            analyticsApiService.getBranchFinancialAnalytics(branchUuid.toString(), "2026-10-01", "2026-10-07")
        } returns Response.success(listOf(sampleSnapshotDto))

        val result = repository.getBranchFinancialAnalytics(branchId, "2026-10-01", "2026-10-07")

        assertTrue(result is AppResult.Success)
        val list = (result as AppResult.Success).data
        assertEquals(1, list.size)
        assertEquals(Money(950.00), list[0].totalRevenue)
        coVerify(exactly = 1) {
            analyticsApiService.getBranchFinancialAnalytics(branchUuid.toString(), "2026-10-01", "2026-10-07")
        }
    }

    @Test
    fun `getBranchFinancialAnalytics fallback to sessionDataStore when branchId is null`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery {
            analyticsApiService.getBranchFinancialAnalytics(branchUuid.toString(), null, null)
        } returns Response.success(listOf(sampleSnapshotDto))

        val result = repository.getBranchFinancialAnalytics(null, null, null)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        coVerify(exactly = 1) {
            analyticsApiService.getBranchFinancialAnalytics(branchUuid.toString(), null, null)
        }
    }

    @Test
    fun `getBranchFinancialAnalytics returns failure when no active branch in session`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = repository.getBranchFinancialAnalytics(null, null, null)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { analyticsApiService.getBranchFinancialAnalytics(any(), any(), any()) }
    }

    @Test
    fun `getNetworkAnalyticsSummary returns mapped domain summary`() = runTest(testDispatcher) {
        coEvery { analyticsApiService.getNetworkSummary() } returns Response.success(sampleNetworkSummaryDto)

        val result = repository.getNetworkAnalyticsSummary()

        assertTrue(result is AppResult.Success)
        val summary = (result as AppResult.Success).data
        assertEquals(3, summary.totalActiveBranchesCount)
        assertEquals(Money(4850.00), summary.totalNetworkRevenue)
        coVerify(exactly = 1) { analyticsApiService.getNetworkSummary() }
    }

    @Test
    fun `server error 403 maps to Forbidden error`() = runTest(testDispatcher) {
        val errorResponse = Response.error<NetworkSummaryResourceDto>(
            403,
            "{\"code\":\"ACCESS_DENIED\",\"message\":\"Forbidden\"}".toResponseBody("application/json".toMediaType())
        )
        coEvery { analyticsApiService.getNetworkSummary() } returns errorResponse

        val result = repository.getNetworkAnalyticsSummary()

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Forbidden)
    }

    @Test
    fun `server error 404 maps to NotFound error`() = runTest(testDispatcher) {
        val errorResponse = Response.error<BranchDailySummaryResourceDto>(
            404,
            "{\"code\":\"NOT_FOUND\",\"message\":\"Branch not found\"}".toResponseBody("application/json".toMediaType())
        )
        coEvery { analyticsApiService.getBranchDailySummary(branchUuid.toString()) } returns errorResponse

        val result = repository.getBranchDailySummary(branchId)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.NotFound)
    }
}
