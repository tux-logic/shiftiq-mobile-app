package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.database.entity.TelemetrySnapshotEntity
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.DemoVehicleIoTProfiles
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
import kotlin.random.Random

@HiltViewModel
class DriverHomeViewModel @Inject constructor(
    private val getDriverDashboardUseCase: GetDriverDashboardUseCase,
    private val telemetryDao: TelemetryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverHomeUiState(isLoading = true))
    val uiState: StateFlow<DriverHomeUiState> = _uiState.asStateFlow()

    private var telemetrySimulationJob: Job? = null

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val data = getDriverDashboardUseCase()
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
                startLiveTelemetrySimulation()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "No fue posible cargar datos del vehículo"
                    )
                }
                startLiveTelemetrySimulation()
            }
        }
    }

    private fun startLiveTelemetrySimulation() {
        telemetrySimulationJob?.cancel()
        telemetrySimulationJob = viewModelScope.launch {
            while (isActive) {
                delay(2000)
                val currentState = _uiState.value
                val vehicle = currentState.selectedVehicle ?: continue
                val profileTelemetry = DemoVehicleIoTProfiles
                    .forVehicle(vehicle, currentState.availableVehicles)
                    ?.telemetry
                    ?: continue
                val updatedTelemetry = profileTelemetry.copy(
                    rpm = (profileTelemetry.rpm + Random.nextInt(-18, 19)).coerceAtLeast(500),
                    temperatureCelsius = (profileTelemetry.temperatureCelsius + Random.nextInt(-1, 2))
                        .coerceIn(70, 120),
                    batteryVoltage = (profileTelemetry.batteryVoltage + Random.nextInt(-1, 2) * 0.1)
                        .coerceIn(11.5, 14.8)
                )

                _uiState.update { state ->
                    if (state.selectedVehicle?.id == vehicle.id) {
                        state.copy(liveTelemetry = updatedTelemetry)
                    } else state
                }

                try {
                    telemetryDao.insertTelemetry(
                        TelemetrySnapshotEntity(
                            vehicleId = vehicle.id,
                            rpm = updatedTelemetry.rpm,
                            temperatureCelsius = updatedTelemetry.temperatureCelsius,
                            speedKmh = updatedTelemetry.speedKmh,
                            odometerKm = updatedTelemetry.odometerKm,
                            fuelLevelPercent = updatedTelemetry.fuelPercent,
                            batteryVoltage = updatedTelemetry.batteryVoltage
                        )
                    )
                } catch (_: Exception) {
                    // Demo telemetry is best effort and must not block the dashboard.
                }
            }
        }
    }

    fun onSelectVehicle(vehicle: B2CVehicleItem) {
        val currentState = _uiState.value
        val profile = DemoVehicleIoTProfiles.forVehicle(vehicle, currentState.availableVehicles)
        _uiState.update {
            it.copy(
                selectedVehicle = vehicle,
                isVehicleSelectorOpen = false,
                liveTelemetry = profile?.telemetry ?: DriverLiveTelemetry(isSynchronized = false),
                vehicleHealth = profile?.health ?: it.vehicleHealth,
                activeDtcAlert = profile?.alert,
                isObdConnected = false
            )
        }
    }

    fun toggleVehicleSelector() {
        _uiState.update { it.copy(isVehicleSelectorOpen = !it.isVehicleSelectorOpen) }
    }

    fun onStartFullScan() {
        val initialState = _uiState.value
        if (initialState.isScanning) return
        val selectedVehicle = initialState.selectedVehicle ?: return
        val profile = DemoVehicleIoTProfiles.forVehicle(selectedVehicle, initialState.availableVehicles)
            ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanProgressMessage = "Iniciando escaneo IoT de demostración...") }
            delay(1200)
            _uiState.update { it.copy(scanProgressMessage = "Simulando telemetría para el vehículo seleccionado...") }
            delay(1500)
            _uiState.update { it.copy(scanProgressMessage = "Generando alertas DTC de demostración...") }
            delay(1500)
            _uiState.update {
                if (it.selectedVehicle?.id != selectedVehicle.id) {
                    it.copy(isScanning = false, scanProgressMessage = null)
                } else {
                    it.copy(
                        isScanning = false,
                        scanProgressMessage = null,
                        liveTelemetry = profile.telemetry,
                        vehicleHealth = profile.health,
                        activeDtcAlert = profile.alert,
                        isObdConnected = false
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        telemetrySimulationJob?.cancel()
    }
}
