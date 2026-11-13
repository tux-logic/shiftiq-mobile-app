package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleTelemetryHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val getVehicleTelemetryHistory: GetVehicleTelemetryHistoryUseCase,
    private val workOrderRepository: WorkOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GarageUiState(isLoading = true))
    val uiState: StateFlow<GarageUiState> = _uiState.asStateFlow()

    fun loadGarageData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, historyErrorMessage = null) }

            val userId = sessionDataStore.userId.firstOrNull()
            if (userId == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Your customer session was not found. Please sign in again.")
                }
                return@launch
            }

            when (val profileResult = getCustomerByUserId(userId.value.toString())) {
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = profileResult.error.message) }
                }

                is AppResult.Success -> {
                    when (val vehiclesResult = getCustomerVehicles(CustomerId(profileResult.data.id))) {
                        is AppResult.Failure -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = vehiclesResult.error.message) }
                        }

                        is AppResult.Success -> {
                            val vehicles = vehiclesResult.data
                            val telemetryByVehicle = vehicles.associate { vehicle ->
                                val telemetry = getVehicleTelemetryHistory(vehicle.id, page = 0, size = 1)
                                vehicle.id.toString() to (telemetry as? AppResult.Success)?.data?.firstOrNull()
                            }

                            val historyItems = mutableListOf<MaintenanceHistoryItem>()
                            var historyFailed = false
                            vehicles.forEach { vehicle ->
                                when (val result = workOrderRepository.getWorkOrders(vehicleId = vehicle.id)) {
                                    is AppResult.Success -> {
                                        historyItems += result.data
                                            .filter { it.status == WorkOrderStatus.COMPLETED }
                                            .map { order ->
                                                MaintenanceHistoryItem(
                                                    id = order.id.value.toString(),
                                                    date = order.completedAt ?: order.updatedAt,
                                                    title = order.formattedNumber,
                                                    mileage = order.mileageIn,
                                                    workshopName = "Taller no disponible",
                                                    description = order.diagnosticSummary,
                                                    totalCostPen = order.totalAmount.amount.toDouble(),
                                                    hasTechnicalDetail = order.diagnosticSummary.isNotBlank()
                                                )
                                            }
                                    }

                                    is AppResult.Failure -> historyFailed = true
                                }
                            }

                            val latestTelemetry = vehicles.firstOrNull()?.let { telemetryByVehicle[it.id.toString()] }
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    vehicles = vehicles.map { vehicle ->
                                        B2CVehicleItem(
                                            id = vehicle.id.toString(),
                                            brand = vehicle.brand,
                                            model = vehicle.model,
                                            year = vehicle.year,
                                            plateNumber = vehicle.plateNumber.value,
                                            vin = vehicle.vin.value,
                                            photoUrl = vehicle.photoUrl,
                                            isMonitored = false,
                                            batteryVoltage = null,
                                            isPrimary = false
                                        )
                                    },
                                    currentOdometerKm = latestTelemetry?.odometerKm?.toInt(),
                                    nextServiceKm = null,
                                    batteryHealthVoltage = null,
                                    engineOilLifePercent = null,
                                    maintenanceHistory = historyItems.sortedByDescending { item -> item.date },
                                    historyErrorMessage = if (historyFailed) {
                                        "Some service history could not be loaded from the server."
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
