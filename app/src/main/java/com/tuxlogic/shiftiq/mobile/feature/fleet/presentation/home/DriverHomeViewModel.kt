package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.database.entity.TelemetrySnapshotEntity
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetDriverDashboardUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
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
                val current = _uiState.value.liveTelemetry
                val randomRpmOffset = Random.nextInt(-20, 25)
                val newRpm = (850 + randomRpmOffset).coerceIn(820, 920)
                val randomTempOffset = Random.nextInt(-1, 2)
                val newTemp = (88 + randomTempOffset).coerceIn(86, 91)
                val randomBatteryOffset = (Random.nextInt(-1, 2)) * 0.1
                val newBattery = ((13.8 + randomBatteryOffset) * 10).toInt() / 10.0

                val updatedTelemetry = current.copy(
                    rpm = newRpm,
                    temperatureCelsius = newTemp,
                    batteryVoltage = newBattery
                )

                _uiState.update { it.copy(liveTelemetry = updatedTelemetry) }

                // Persistir en Room para offline cache
                _uiState.value.selectedVehicle?.let { vehicle ->
                    try {
                        telemetryDao.insertTelemetry(
                            TelemetrySnapshotEntity(
                                vehicleId = vehicle.id,
                                rpm = newRpm,
                                temperatureCelsius = newTemp,
                                speedKmh = updatedTelemetry.speedKmh,
                                odometerKm = updatedTelemetry.odometerKm,
                                fuelLevelPercent = updatedTelemetry.fuelPercent,
                                batteryVoltage = newBattery
                            )
                        )
                    } catch (_: Exception) {
                        // Persistencia secundaria sin bloquear UI
                    }
                }
            }
        }
    }

    fun onSelectVehicle(vehicle: B2CVehicleItem) {
        _uiState.update {
            it.copy(
                selectedVehicle = vehicle,
                isVehicleSelectorOpen = false
            )
        }
    }

    fun toggleVehicleSelector() {
        _uiState.update { it.copy(isVehicleSelectorOpen = !it.isVehicleSelectorOpen) }
    }

    fun onStartFullScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanProgressMessage = "Iniciando escaneo OBD-II...") }
            delay(1200)
            _uiState.update { it.copy(scanProgressMessage = "Leyendo parámetros de ECU y bus CAN...") }
            delay(1500)
            _uiState.update { it.copy(scanProgressMessage = "Comprobando códigos DTC de tren motriz...") }
            delay(1500)
            _uiState.update {
                it.copy(
                    isScanning = false,
                    scanProgressMessage = null
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        telemetrySimulationJob?.cancel()
    }
}
