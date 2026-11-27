package com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.BranchDailySnapshotResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.BranchDailySummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.NetworkSummaryResourceDto
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.AnalyticsSnapshotId
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeAssignedWorkOrderSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto.EmployeeAnalyticsSummaryResourceDto

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

fun EmployeeAnalyticsSummaryResourceDto.toDomain(): EmployeeAnalyticsSummary =
    EmployeeAnalyticsSummary(
        employeeId = employeeId,
        branchIds = branchIds.orEmpty(),
        assignedWorkOrdersCount = assignedWorkOrdersCount,
        pendingTasksCount = pendingTasksCount,
        inProgressTasksCount = 0,
        completedTasksCount = completedTasksCount,
        activeDtcAlertsCount = activeDtcAlertsCount,
        assignedWorkOrders = assignedWorkOrders.orEmpty().map { order ->
            EmployeeAssignedWorkOrderSummary(
                workOrderId = order.workOrderId,
                branchId = order.branchId,
                vehicleId = order.vehicleId,
                internalNumber = order.internalNumber,
                status = order.status,
                assignedTasksCount = order.assignedTasksCount,
                pendingTasksCount = order.pendingTasksCount
            )
        }
    )
