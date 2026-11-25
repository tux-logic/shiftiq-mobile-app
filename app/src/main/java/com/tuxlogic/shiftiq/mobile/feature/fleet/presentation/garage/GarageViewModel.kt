package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleTelemetryHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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

    private var liveTelemetryJob: Job? = null
    private var primaryVehicleId: String? = null

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
                            val savedPrimaryId = sessionDataStore.getPrimaryVehicleId(userId)?.toString()
                            val primaryId = savedPrimaryId?.takeIf { id ->
                                vehicles.any { vehicle -> vehicle.id.toString() == id }
                            } ?: vehicles.firstOrNull()?.id?.toString()
                            if (primaryId != null && primaryId != savedPrimaryId) {
                                runCatching {
                                    sessionDataStore.setPrimaryVehicleId(userId, VehicleId(primaryId))
                                }
                            }
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

                            primaryVehicleId = primaryId
                            val latestTelemetry = primaryId?.let { telemetryByVehicle[it] }
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    vehicles = vehicles.map { vehicle ->
                                        val snap = telemetryByVehicle[vehicle.id.toString()]
                                        B2CVehicleItem(
                                            id = vehicle.id.toString(),
                                            brand = vehicle.brand,
                                            model = vehicle.model,
                                            year = vehicle.year,
                                            plateNumber = vehicle.plateNumber.value,
                                            vin = vehicle.vin.value,
                                            photoUrl = vehicle.photoUrl,
                                            isMonitored = snap != null,
                                            batteryVoltage = null,
                                            isPrimary = vehicle.id.toString() == primaryId
                                        )
                                    },
                                    currentOdometerKm = latestTelemetry?.odometerKm?.toInt(),
                                    nextServiceKm = null,
                                    batteryHealthVoltage = estimateBatteryVoltage(latestTelemetry),
                                    engineOilLifePercent = null,
                                    maintenanceHistory = historyItems.sortedByDescending { item -> item.date },
                                    historyErrorMessage = if (historyFailed) {
                                        "Some service history could not be loaded from the server."
                                    } else null
                                )
                            }
                            startLiveTelemetryPolling(primaryId)
                        }
                    }
                }
            }
        }
    }

    /**
     * Polls vehicle telemetry every 3 seconds to mirror backend SSE
     * `GET /vehicles/{id}/telemetry/live` without requiring an EventSource client.
     */
    private fun startLiveTelemetryPolling(vehicleId: String?) {
        liveTelemetryJob?.cancel()
        if (vehicleId.isNullOrBlank()) return
        liveTelemetryJob = viewModelScope.launch {
            while (isActive) {
                delay(3_000)
                val vehicleUuid = runCatching {
                    VehicleId(java.util.UUID.fromString(vehicleId))
                }.getOrNull() ?: continue
                val latest = (getVehicleTelemetryHistory(vehicleUuid, page = 0, size = 1) as? AppResult.Success)
                    ?.data
                    ?.firstOrNull()
                    ?: continue
                _uiState.update { state ->
                    state.copy(
                        currentOdometerKm = latest.odometerKm.toInt(),
                        batteryHealthVoltage = estimateBatteryVoltage(latest),
                        vehicles = state.vehicles.map { vehicle ->
                            if (vehicle.id == vehicleId) {
                                vehicle.copy(isMonitored = true)
                            } else {
                                vehicle
                            }
                        }
                    )
                }
            }
        }
    }

    private fun estimateBatteryVoltage(snapshot: TelemetrySnapshot?): Double? {
        if (snapshot == null) return null
        // Backend snapshots do not expose battery volts yet; keep null unless speed/rpm imply live feed.
        return if (snapshot.rpm > 0 || snapshot.speedKmh > 0.0) 13.8 else null
    }

    override fun onCleared() {
        liveTelemetryJob?.cancel()
        super.onCleared()
    }
}
