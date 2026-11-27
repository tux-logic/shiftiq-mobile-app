package com.tuxlogic.shiftiq.mobile.feature.analytics.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeAssignedWorkOrderSummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.EmployeeDailyCompletion
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetEmployeeAnalyticsSummaryUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrdersUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskStatus
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetEmployeeRegistrationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class EmployeeDashboardUiState(
    val isLoading: Boolean = true,
    val activeBranchName: String = "Sucursal activa",
    val summary: EmployeeAnalyticsSummary? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val getEmployeeAnalyticsSummary: GetEmployeeAnalyticsSummaryUseCase,
    private val getWorkOrders: GetWorkOrdersUseCase,
    private val sessionDataStore: SessionDataStore,
    private val getBranchById: GetBranchByIdUseCase,
    private val getEmployeeRegistrations: GetEmployeeRegistrationsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeDashboardUiState())
    val uiState: StateFlow<EmployeeDashboardUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getEmployeeAnalyticsSummary()) {
                is AppResult.Success -> {
                    val branchId = sessionDataStore.activeBranchId.first()
                    val branchName = branchId?.let {
                        (getBranchById(it.toString()) as? AppResult.Success)?.data?.name
                    } ?: _uiState.value.activeBranchName
                    val orders = branchId?.let { getWorkOrders(branchId = it) }
                    val summary = if (orders is AppResult.Success) {
                        enrichSummary(result.data, orders.data)
                    } else {
                        result.data
                    }
                    _uiState.value = EmployeeDashboardUiState(
                        isLoading = false,
                        activeBranchName = branchName,
                        summary = summary
                    )
                }
                is AppResult.Failure -> _uiState.value = EmployeeDashboardUiState(
                    isLoading = false,
                    errorMessage = result.error.message
                )
            }
        }
    }

    private suspend fun enrichSummary(
        summary: EmployeeAnalyticsSummary,
        workOrders: List<com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder>
    ): EmployeeAnalyticsSummary {
        val employeeIds = mutableSetOf(summary.employeeId)
        val employeeId = runCatching { java.util.UUID.fromString(summary.employeeId) }.getOrNull()
        if (employeeId != null) {
            val registrations = getEmployeeRegistrations(employeeId = employeeId)
            if (registrations is AppResult.Success) {
                employeeIds += registrations.data.map { it.id.toString() }
            }
        }
        val assignedTasksByOrder = workOrders.mapNotNull { order ->
            val tasks = order.tasks.filter { it.assignedMechanicId?.toString() in employeeIds }
            if (tasks.isEmpty()) null else order to tasks
        }
        if (assignedTasksByOrder.isEmpty()) return summary

        val today = LocalDate.now()
        val dailyCounts = (6 downTo 0).associate { today.minusDays(it.toLong()) to 0 }.toMutableMap()
        var durationTotalMinutes = 0L
        var durationSampleCount = 0
        var completedCount = 0
        var inProgressCount = 0
        var pendingCount = 0
        val assignedOrders = assignedTasksByOrder.map { (order, tasks) ->
            val pending = tasks.count { it.status == TaskStatus.PENDING }
            tasks.forEach { task ->
                when (task.status) {
                    TaskStatus.COMPLETED -> {
                        completedCount++
                        val completed = parseInstant(task.completedAt)
                        if (completed != null) {
                            val date = completed.atZone(ZoneId.systemDefault()).toLocalDate()
                            if (date in dailyCounts) dailyCounts[date] = dailyCounts.getValue(date) + 1
                            val started = parseInstant(task.startedAt)
                            if (started != null && !completed.isBefore(started)) {
                                durationTotalMinutes += Duration.between(started, completed).toMinutes()
                                durationSampleCount++
                            }
                        }
                    }
                    TaskStatus.DOING -> inProgressCount++
                    TaskStatus.PENDING -> pendingCount++
                    TaskStatus.CANCELLED -> Unit
                }
            }
            EmployeeAssignedWorkOrderSummary(
                workOrderId = order.id.toString(),
                branchId = order.branchId.toString(),
                vehicleId = order.vehicleId.toString(),
                internalNumber = order.internalNumber,
                status = order.status.name,
                assignedTasksCount = tasks.size,
                pendingTasksCount = pending
            )
        }
        return summary.copy(
            branchIds = summary.branchIds.ifEmpty { listOf(assignedOrders.first().branchId) },
            assignedWorkOrdersCount = assignedOrders.size,
            pendingTasksCount = pendingCount,
            inProgressTasksCount = inProgressCount,
            completedTasksCount = completedCount,
            assignedWorkOrders = assignedOrders,
            weeklyCompletedTasks = dailyCounts.map { (date, count) -> EmployeeDailyCompletion(date.toString(), count) },
            avgCompletionMinutes = durationTotalMinutes.takeIf { durationSampleCount > 0 }
                ?.toDouble()?.div(durationSampleCount)
        )
    }

    private fun parseInstant(value: String?): Instant? =
        value?.let { runCatching { Instant.parse(it) }.getOrNull() }
}
