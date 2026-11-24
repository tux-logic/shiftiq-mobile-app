package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.database.entity.TelemetrySnapshotEntity
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetDriverDashboardUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverHomeViewModel @Inject constructor(
    private val getDriverDashboardUseCase: GetDriverDashboardUseCase,
    private val telemetryDao: TelemetryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverHomeUiState(isLoading = true))
    val uiState: StateFlow<DriverHomeUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        loadDashboardData()
        startPeriodicRefresh()
    }

    fun loadDashboardData(selectedVehicleId: String? = _uiState.value.selectedVehicle?.id) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val data = getDriverDashboardUseCase(selectedVehicleId)
                cacheTelemetry(data.selectedVehicle?.id, data.liveTelemetry)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        driverName = data.driverName,
                        driverEmail = data.driverEmail,
                        driverRole = data.driverRole,
                        selectedVehicle = data.selectedVehicle,
                        availableVehicles = data.availableVehicles,
                        vehicleHealth = data.vehicleHealth,
                        liveTelemetry = data.liveTelemetry,
                        activeService = data.activeService,
                        activeDtcAlert = data.activeDtcAlert,
                        isObdConnected = data.isObdConnected
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unable to load vehicle dashboard data"
                    )
                }
            }
        }
    }

    private fun startPeriodicRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(15_000)
                val selectedId = _uiState.value.selectedVehicle?.id
                runCatching {
                    val data = getDriverDashboardUseCase(selectedId)
                    cacheTelemetry(data.selectedVehicle?.id, data.liveTelemetry)
                    _uiState.update {
                        it.copy(
                            selectedVehicle = data.selectedVehicle,
                            availableVehicles = data.availableVehicles,
                            vehicleHealth = data.vehicleHealth,
                            liveTelemetry = data.liveTelemetry,
                            activeService = data.activeService,
                            activeDtcAlert = data.activeDtcAlert,
                            isObdConnected = data.isObdConnected
                        )
                    }
                }
            }
        }
    }

    fun onSelectVehicle(vehicle: B2CVehicleItem) {
        _uiState.update { it.copy(isVehicleSelectorOpen = false, selectedVehicle = vehicle) }
        loadDashboardData(vehicle.id)
    }

    fun toggleVehicleSelector() {
        _uiState.update { it.copy(isVehicleSelectorOpen = !it.isVehicleSelectorOpen) }
    }

    fun onStartFullScan() {
        val selectedVehicle = _uiState.value.selectedVehicle ?: return
        if (_uiState.value.isScanning) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isScanning = true, scanProgressMessage = "Refreshing OBD-II telemetry from the server...")
            }
            delay(400)
            val data = getDriverDashboardUseCase(selectedVehicle.id)
            _uiState.update {
                it.copy(
                    isScanning = false,
                    scanProgressMessage = null,
                    selectedVehicle = data.selectedVehicle,
                    availableVehicles = data.availableVehicles,
                    vehicleHealth = data.vehicleHealth,
                    liveTelemetry = data.liveTelemetry,
                    activeService = data.activeService,
                    activeDtcAlert = data.activeDtcAlert,
                    isObdConnected = data.isObdConnected,
                    errorMessage = if (!data.isObdConnected) {
                        "No telemetry snapshots were found for this vehicle yet."
                    } else null
                )
            }
        }
    }

    private suspend fun cacheTelemetry(vehicleId: String?, telemetry: DriverLiveTelemetry) {
        if (vehicleId.isNullOrBlank() || !telemetry.isSynchronized) return
        runCatching {
            telemetryDao.insertTelemetry(
                TelemetrySnapshotEntity(
                    vehicleId = vehicleId,
                    rpm = telemetry.rpm,
                    temperatureCelsius = telemetry.temperatureCelsius,
                    speedKmh = telemetry.speedKmh,
                    odometerKm = telemetry.odometerKm,
                    fuelLevelPercent = telemetry.fuelPercent,
                    batteryVoltage = telemetry.batteryVoltage
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
