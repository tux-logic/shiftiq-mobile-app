package com.tuxlogic.shiftiq.mobile.feature.analytics.data.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.dto.BranchDailySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.dto.BranchDailySummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.data.remote.dto.NetworkSummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.AnalyticsSnapshotId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary

fun BranchDailySummaryResourceDto.toDomain(): BranchDailySummary = BranchDailySummary(
    branchId = BranchId(branchId),
    totalRevenue = Money(totalRevenue),
    completedWorkOrdersCount = completedWorkOrdersCount,
    totalAppointmentsCount = totalAppointmentsCount,
    lowStockAlertsCount = lowStockAlertsCount,
    dtcAlertsCount = dtcAlertsCount
)

fun BranchDailySnapshotResourceDto.toDomain(): BranchDailySnapshot = BranchDailySnapshot(
    id = AnalyticsSnapshotId(id),
    branchId = BranchId(branchId),
    snapshotDate = snapshotDate,
    totalRevenue = Money(totalRevenue),
    completedWorkOrdersCount = completedWorkOrdersCount,
    totalAppointmentsCount = totalAppointmentsCount,
    lowStockAlertsCount = lowStockAlertsCount,
    dtcAlertsCount = dtcAlertsCount
)

fun NetworkSummaryResourceDto.toDomain(): NetworkAnalyticsSummary = NetworkAnalyticsSummary(
    totalActiveBranchesCount = totalActiveBranchesCount,
    totalNetworkRevenue = Money(totalNetworkRevenue),
    totalNetworkCompletedWorkOrders = totalNetworkCompletedWorkOrders,
    totalNetworkAppointments = totalNetworkAppointments,
    totalNetworkDtcAlerts = totalNetworkDtcAlerts
)
