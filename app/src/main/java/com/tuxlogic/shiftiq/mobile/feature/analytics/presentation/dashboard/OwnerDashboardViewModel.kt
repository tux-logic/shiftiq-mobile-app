package com.tuxlogic.shiftiq.mobile.feature.analytics.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetBranchDailySummaryUseCase
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetBranchFinancialAnalyticsUseCase
import com.tuxlogic.shiftiq.mobile.feature.analytics.application.usecase.GetNetworkAnalyticsSummaryUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySnapshot
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.BranchDailySummary
import com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model.NetworkAnalyticsSummary
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrdersUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehicleByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class OwnerDashboardUiState(
    val isLoading: Boolean = true,
    val activeBranchName: String = "Sucursal activa",
    val branchSummary: BranchDailySummary? = null,
    val networkSummary: NetworkAnalyticsSummary? = null,
    val financialSnapshots: List<BranchDailySnapshot> = emptyList(),
    val recentWorkOrders: List<OwnerDashboardWorkOrderPreview> = emptyList(),
    val selectedRangeDays: Int = 14,
    val errorMessage: String? = null
)

data class OwnerDashboardWorkOrderPreview(
    val order: WorkOrder,
    val vehicleDisplay: String
)

@HiltViewModel
class OwnerDashboardViewModel @Inject constructor(
    private val getBranchDailySummary: GetBranchDailySummaryUseCase,
    private val getNetworkAnalyticsSummary: GetNetworkAnalyticsSummaryUseCase,
    private val getBranchFinancialAnalytics: GetBranchFinancialAnalyticsUseCase,
    private val getWorkOrders: GetWorkOrdersUseCase,
    private val getBranchById: GetBranchByIdUseCase,
    private val getVehicleById: GetVehicleByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerDashboardUiState())
    val uiState: StateFlow<OwnerDashboardUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val branchResult = getBranchDailySummary()
            val networkResult = getNetworkAnalyticsSummary()
            val branch = (branchResult as? AppResult.Success)?.data
            val network = (networkResult as? AppResult.Success)?.data
            val error = (branchResult as? AppResult.Failure)?.error?.message
            val branchName = branch?.let { summary ->
                (getBranchById(summary.branchId.toString()) as? AppResult.Success)?.data?.name
            } ?: _uiState.value.activeBranchName
            val rangeDays = _uiState.value.selectedRangeDays
            val today = LocalDate.now()
            val financial = branch?.let {
                getBranchFinancialAnalytics(
                    branchId = it.branchId,
                    startDate = today.minusDays((rangeDays - 1).toLong()).toString(),
                    endDate = today.toString()
                )
            }
            val orders = branch?.let { getWorkOrders(branchId = it.branchId) }
            val previews = coroutineScope {
                (orders as? AppResult.Success)?.data.orEmpty().take(4).map { order ->
                    async {
                        val vehicle = (getVehicleById(order.vehicleId) as? AppResult.Success)?.data
                        val vehicleDisplay = vehicle?.let {
                            val name = listOf(it.brand, it.model).filter(String::isNotBlank).joinToString(" ")
                            "$name (${it.plateNumber.value})".trim()
                        } ?: "Vehículo ${order.vehicleId}"
                        OwnerDashboardWorkOrderPreview(order, vehicleDisplay)
                    }
                }.awaitAll()
            }
            _uiState.value = OwnerDashboardUiState(
                isLoading = false,
                activeBranchName = branchName,
                branchSummary = branch,
                networkSummary = network,
                financialSnapshots = (financial as? AppResult.Success)?.data.orEmpty().sortedBy { it.snapshotDate },
                recentWorkOrders = previews,
                selectedRangeDays = rangeDays,
                errorMessage = error
            )
        }
    }

    fun setRangeDays(days: Int) {
        if (days == _uiState.value.selectedRangeDays) return
        _uiState.value = _uiState.value.copy(selectedRangeDays = days)
        refresh()
    }
}
