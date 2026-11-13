package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehicleByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetWorkOrderByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LiveWorkOrderViewModel @Inject constructor(
    private val getWorkOrderById: GetWorkOrderByIdUseCase,
    private val getVehicleById: GetVehicleByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveWorkOrderUiState(isLoading = true))
    val uiState: StateFlow<LiveWorkOrderUiState> = _uiState.asStateFlow()

    fun loadWorkOrder(id: String) {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull()
        if (uuid == null) {
            _uiState.value = LiveWorkOrderUiState(errorMessage = "This work order has no valid server identifier.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LiveWorkOrderUiState(isLoading = true)
            when (val result = getWorkOrderById(WorkOrderId(uuid))) {
                is AppResult.Failure -> _uiState.value = LiveWorkOrderUiState(errorMessage = result.error.message)
                is AppResult.Success -> {
                    val order = result.data
                    val vehicleDescription = when (val vehicleResult = getVehicleById(order.vehicleId)) {
                        is AppResult.Success -> "${vehicleResult.data.brand} ${vehicleResult.data.model} ${vehicleResult.data.year} • ${vehicleResult.data.plateNumber.value}"
                        is AppResult.Failure -> "Vehicle information is unavailable."
                    }
                    _uiState.value = LiveWorkOrderUiState(
                        orderNumber = order.formattedNumber,
                        vehicleDescription = vehicleDescription,
                        status = order.status.name,
                        diagnosticSummary = order.diagnosticSummary,
                        mileageIn = order.mileageIn,
                        totalAmount = order.totalAmount.formatted(),
                        tasks = order.tasks.map { task ->
                            LiveWorkOrderTaskItem(
                                title = task.description.ifBlank { task.serviceName.orEmpty() },
                                status = task.status.name,
                                price = task.price.formatted(),
                                mechanicName = task.assignedMechanicName,
                                evidenceCount = task.evidenceImages.size
                            )
                        }
                    )
                }
            }
        }
    }
}
