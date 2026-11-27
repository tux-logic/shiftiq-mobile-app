package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetEmployeeAnalyticsSummaryUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrdersUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkOrderListUiState(
    val isLoading: Boolean = true,
    val orders: List<WorkOrder> = emptyList(),
    val selectedStatus: WorkOrderStatus? = null,
    val errorMessage: String? = null
) {
    val filteredOrders: List<WorkOrder>
        get() = selectedStatus?.let { selected -> orders.filter { it.status == selected } } ?: orders
    fun countByStatus(status: WorkOrderStatus?): Int =
        status?.let { selected -> orders.count { it.status == selected } } ?: orders.size
}

@HiltViewModel
class WorkOrderListViewModel @Inject constructor(
    private val getWorkOrders: GetWorkOrdersUseCase,
    private val getEmployeeSummary: GetEmployeeAnalyticsSummaryUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkOrderListUiState())
    val uiState: StateFlow<WorkOrderListUiState> = _uiState.asStateFlow()

    fun selectStatus(status: WorkOrderStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val branchId = sessionDataStore.activeBranchId.first()
            if (branchId == null) {
                _uiState.value = WorkOrderListUiState(isLoading = false, errorMessage = "Select an active branch to view work orders.")
                return@launch
            }

            when (val result = getWorkOrders(branchId = branchId)) {
                is AppResult.Failure -> _uiState.value = WorkOrderListUiState(
                    isLoading = false,
                    errorMessage = result.error.message
                )
                is AppResult.Success -> {
                    val role = sessionDataStore.userRole.first()
                    val orders = if (role in employeeRoles) {
                        when (val summary = getEmployeeSummary()) {
                            is AppResult.Success -> {
                                val assignedIds = summary.data.assignedWorkOrders
                                    .mapTo(mutableSetOf()) { it.workOrderId }
                                result.data.filter { it.id.toString() in assignedIds }
                            }
                            is AppResult.Failure -> emptyList()
                        }
                    } else {
                        result.data
                    }
                    _uiState.value = WorkOrderListUiState(isLoading = false, orders = orders)
                }
            }
        }
    }

    private companion object {
        val employeeRoles = setOf(Role.ROLE_EMPLOYEE, Role.ROLE_BRANCH_MANAGER, Role.ROLE_ASSISTANT)
    }
}
