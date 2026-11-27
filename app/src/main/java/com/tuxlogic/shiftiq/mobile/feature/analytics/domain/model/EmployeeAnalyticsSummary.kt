package com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model

data class EmployeeAnalyticsSummary(
    val employeeId: String,
    val branchIds: List<String>,
    val assignedWorkOrdersCount: Int,
    val pendingTasksCount: Int,
    val inProgressTasksCount: Int,
    val completedTasksCount: Int,
    val activeDtcAlertsCount: Int,
    val assignedWorkOrders: List<EmployeeAssignedWorkOrderSummary>,
    val weeklyCompletedTasks: List<EmployeeDailyCompletion> = emptyList(),
    val avgCompletionMinutes: Double? = null
)

data class EmployeeDailyCompletion(val date: String, val count: Int)

data class EmployeeAssignedWorkOrderSummary(
    val workOrderId: String,
    val branchId: String,
    val vehicleId: String,
    val internalNumber: Int,
    val status: String,
    val assignedTasksCount: Int,
    val pendingTasksCount: Int
)
