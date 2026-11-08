package com.tuxlogic.shiftiq.mobile.feature.analytics

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.dto.BranchDailySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.dto.BranchDailySummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.dto.NetworkSummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.AnalyticsSnapshotId
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class AnalyticsMappersTest {

    @Test
    fun `BranchDailySummaryResourceDto toDomain maps all fields properly`() {
        val branchUuid = UUID.randomUUID()
        val dto = BranchDailySummaryResourceDto(
            branchId = branchUuid.toString(),
            totalRevenue = 1250.50,
            completedWorkOrdersCount = 4,
            totalAppointmentsCount = 7,
            lowStockAlertsCount = 2,
            dtcAlertsCount = 1
        )

        val domain = dto.toDomain()

        assertEquals(BranchId(branchUuid), domain.branchId)
        assertEquals(Money(1250.50), domain.totalRevenue)
        assertEquals(4, domain.completedWorkOrdersCount)
        assertEquals(7, domain.totalAppointmentsCount)
        assertEquals(2, domain.lowStockAlertsCount)
        assertEquals(1, domain.dtcAlertsCount)
    }

    @Test
    fun `BranchDailySnapshotResourceDto toDomain maps all fields properly`() {
        val snapshotUuid = UUID.randomUUID()
        val branchUuid = UUID.randomUUID()
        val dto = BranchDailySnapshotResourceDto(
            id = snapshotUuid.toString(),
            branchId = branchUuid.toString(),
            snapshotDate = "2026-10-07",
            totalRevenue = 950.00,
            completedWorkOrdersCount = 3,
            totalAppointmentsCount = 5,
            lowStockAlertsCount = 1,
            dtcAlertsCount = 0
        )

        val domain = dto.toDomain()

        assertEquals(AnalyticsSnapshotId(snapshotUuid), domain.id)
        assertEquals(BranchId(branchUuid), domain.branchId)
        assertEquals("2026-10-07", domain.snapshotDate)
        assertEquals(Money(950.00), domain.totalRevenue)
        assertEquals(3, domain.completedWorkOrdersCount)
        assertEquals(5, domain.totalAppointmentsCount)
        assertEquals(1, domain.lowStockAlertsCount)
        assertEquals(0, domain.dtcAlertsCount)
    }

    @Test
    fun `NetworkSummaryResourceDto toDomain maps network aggregation fields`() {
        val dto = NetworkSummaryResourceDto(
            totalActiveBranchesCount = 3,
            totalNetworkRevenue = 4850.75,
            totalNetworkCompletedWorkOrders = 14,
            totalNetworkAppointments = 22,
            totalNetworkDtcAlerts = 3
        )

        val domain = dto.toDomain()

        assertEquals(3, domain.totalActiveBranchesCount)
        assertEquals(Money(4850.75), domain.totalNetworkRevenue)
        assertEquals(14, domain.totalNetworkCompletedWorkOrders)
        assertEquals(22, domain.totalNetworkAppointments)
        assertEquals(3, domain.totalNetworkDtcAlerts)
    }
}
