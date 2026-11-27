package com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class BranchDailySummaryResourceDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("completedWorkOrdersCount") val completedWorkOrdersCount: Int,
    @SerializedName("totalAppointmentsCount") val totalAppointmentsCount: Int,
    @SerializedName("lowStockAlertsCount") val lowStockAlertsCount: Int,
    @SerializedName("dtcAlertsCount") val dtcAlertsCount: Int
)

data class BranchDailySnapshotResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("snapshotDate") val snapshotDate: String,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("completedWorkOrdersCount") val completedWorkOrdersCount: Int,
    @SerializedName("totalAppointmentsCount") val totalAppointmentsCount: Int,
    @SerializedName("lowStockAlertsCount") val lowStockAlertsCount: Int,
    @SerializedName("dtcAlertsCount") val dtcAlertsCount: Int
)

data class NetworkSummaryResourceDto(
    @SerializedName("totalActiveBranchesCount") val totalActiveBranchesCount: Int,
    @SerializedName("totalNetworkRevenue") val totalNetworkRevenue: Double,
    @SerializedName("totalNetworkCompletedWorkOrders") val totalNetworkCompletedWorkOrders: Int,
    @SerializedName("totalNetworkAppointments") val totalNetworkAppointments: Int,
    @SerializedName("totalNetworkDtcAlerts") val totalNetworkDtcAlerts: Int
)

data class EmployeeAnalyticsSummaryResourceDto(
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("branchIds") val branchIds: List<String>? = null,
    @SerializedName("assignedWorkOrdersCount") val assignedWorkOrdersCount: Int = 0,
    @SerializedName("pendingTasksCount") val pendingTasksCount: Int = 0,
    @SerializedName("completedTasksCount") val completedTasksCount: Int = 0,
    @SerializedName("activeDtcAlertsCount") val activeDtcAlertsCount: Int = 0,
    @SerializedName("assignedWorkOrders") val assignedWorkOrders: List<EmployeeAssignedWorkOrderResourceDto>? = null
)

data class EmployeeAssignedWorkOrderResourceDto(
    @SerializedName("workOrderId") val workOrderId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("internalNumber") val internalNumber: Int = 0,
    @SerializedName("status") val status: String = "PENDING",
    @SerializedName("assignedTasksCount") val assignedTasksCount: Int = 0,
    @SerializedName("pendingTasksCount") val pendingTasksCount: Int = 0
)
