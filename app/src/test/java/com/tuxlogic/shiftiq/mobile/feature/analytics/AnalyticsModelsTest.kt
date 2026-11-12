package com.tuxlogic.shiftiq.mobile.feature.analytics

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.AnalyticsSnapshotId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class AnalyticsModelsTest {

    @Test
    fun `AnalyticsSnapshotId handles valid and invalid UUID strings`() {
        val uuid = UUID.randomUUID()
        val idFromUuid = AnalyticsSnapshotId(uuid)
        val idFromStr = AnalyticsSnapshotId(uuid.toString())

        assertEquals(idFromUuid, idFromStr)
        assertEquals(uuid.toString(), idFromStr.toString())

        val fallbackId = AnalyticsSnapshotId("non-uuid-string")
        assertEquals(UUID.nameUUIDFromBytes("non-uuid-string".toByteArray()), fallbackId.value)
    }

    @Test
    fun `BranchDailySummary instantiates correctly with domain Money`() {
        val branchId = BranchId(UUID.randomUUID())
        val summary = BranchDailySummary(
            branchId = branchId,
            totalRevenue = Money(1250.0),
            completedWorkOrdersCount = 4,
            totalAppointmentsCount = 7,
            lowStockAlertsCount = 2,
            dtcAlertsCount = 1
        )

        assertEquals(branchId, summary.branchId)
        assertEquals(Money(1250.0), summary.totalRevenue)
        assertEquals(4, summary.completedWorkOrdersCount)
        assertEquals(7, summary.totalAppointmentsCount)
        assertEquals(2, summary.lowStockAlertsCount)
        assertEquals(1, summary.dtcAlertsCount)
    }

    @Test
    fun `BranchDailySnapshot holds daily snapshot metrics correctly`() {
        val snapshotId = AnalyticsSnapshotId(UUID.randomUUID())
        val branchId = BranchId(UUID.randomUUID())
        val snapshot = BranchDailySnapshot(
            id = snapshotId,
            branchId = branchId,
            snapshotDate = "2026-10-07",
            totalRevenue = Money(950.0),
            completedWorkOrdersCount = 3,
            totalAppointmentsCount = 5,
            lowStockAlertsCount = 1,
            dtcAlertsCount = 0
        )

        assertEquals(snapshotId, snapshot.id)
        assertEquals(branchId, snapshot.branchId)
        assertEquals("2026-10-07", snapshot.snapshotDate)
        assertEquals(Money(950.0), snapshot.totalRevenue)
        assertEquals(3, snapshot.completedWorkOrdersCount)
        assertEquals(5, snapshot.totalAppointmentsCount)
        assertEquals(1, snapshot.lowStockAlertsCount)
        assertEquals(0, snapshot.dtcAlertsCount)
    }

    @Test
    fun `NetworkAnalyticsSummary aggregates metrics across network`() {
        val summary = NetworkAnalyticsSummary(
            totalActiveBranchesCount = 3,
            totalNetworkRevenue = Money(4850.0),
            totalNetworkCompletedWorkOrders = 14,
            totalNetworkAppointments = 22,
            totalNetworkDtcAlerts = 3
        )

        assertEquals(3, summary.totalActiveBranchesCount)
        assertEquals(Money(4850.0), summary.totalNetworkRevenue)
        assertEquals(14, summary.totalNetworkCompletedWorkOrders)
        assertEquals(22, summary.totalNetworkAppointments)
        assertEquals(3, summary.totalNetworkDtcAlerts)
    }
}
