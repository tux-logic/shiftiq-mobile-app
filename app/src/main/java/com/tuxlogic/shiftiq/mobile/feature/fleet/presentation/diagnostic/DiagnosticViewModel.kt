package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleDtcAlertsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleTelemetryHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val getVehicleTelemetryHistory: GetVehicleTelemetryHistoryUseCase,
    private val getVehicleDtcAlerts: GetVehicleDtcAlertsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticUiState())
    val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

    private var activeVehicleId: VehicleId? = null

    init {
        refreshDiagnostics()
    }

    fun refreshDiagnostics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanProgress = 0.2f) }
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            if (userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        scanProgress = 0f,
                        dtcResolvedMessage = "Your session expired. Please sign in again."
                    )
                }
                return@launch
            }

            val customer = when (val result = getCustomerByUserId(userId)) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isScanning = false, scanProgress = 0f, dtcResolvedMessage = result.error.message)
                    }
                    return@launch
                }
            }

            val vehicles = when (val result = getCustomerVehicles(CustomerId(customer.id))) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isScanning = false, scanProgress = 0f, dtcResolvedMessage = result.error.message)
                    }
                    return@launch
                }
            }

            val vehicle = vehicles.firstOrNull()
            if (vehicle == null) {
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        scanProgress = 1f,
                        isObdConnected = false,
                        telemetry = DriverLiveTelemetry(isSynchronized = false),
                        activeDtcAlert = null,
                        dtcResolvedMessage = "Register a vehicle to load OBD-II diagnostics."
                    )
                }
                return@launch
            }

            activeVehicleId = vehicle.id
            _uiState.update { it.copy(scanProgress = 0.6f) }

            val telemetry = when (val result = getVehicleTelemetryHistory(vehicle.id, page = 0, size = 1)) {
                is AppResult.Success -> result.data.firstOrNull()
                is AppResult.Failure -> null
            }
            val alerts = when (val result = getVehicleDtcAlerts(vehicle.id, page = 0, size = 10)) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> emptyList()
            }

            val activeAlert = alerts.firstOrNull()?.let { alert ->
                DriverDtcAlert(
                    code = alert.dtcCode,
                    title = "DTC ${alert.dtcCode}",
                    severity = when (alert.severity) {
                        DtcSeverity.LOW -> "Low"
                        DtcSeverity.MEDIUM -> "Medium"
                        DtcSeverity.HIGH -> "High"
                        DtcSeverity.CRITICAL -> "Critical"
                    },
                    suggestedReviewKm = 0,
                    description = alert.description.ifBlank { "Diagnostic trouble code reported by the vehicle." }
                )
            }
            val inspectedEcus = if (alerts.isEmpty()) {
                listOf(
                    EcuInspectionItem(
                        ecuId = "ECU",
                        moduleName = "Powertrain",
                        hasDtc = false,
                        dtcCode = null,
                        statusText = if (telemetry != null) "No active DTC codes" else "No OBD data"
                    )
                )
            } else {
                alerts.mapIndexed { index, alert ->
                    EcuInspectionItem(
                        ecuId = "DTC #${index + 1}",
                        moduleName = alert.description.ifBlank { "Reported module" },
                        hasDtc = true,
                        dtcCode = alert.dtcCode,
                        statusText = "Active DTC ${alert.dtcCode}"
                    )
                }
            }

            _uiState.update {
                it.copy(
                    isScanning = false,
                    scanProgress = 1f,
                    isObdConnected = telemetry != null,
                    telemetry = telemetry?.let { snapshot ->
                        DriverLiveTelemetry(
                            rpm = snapshot.rpm,
                            temperatureCelsius = snapshot.temperature,
                            batteryVoltage = 0.0,
                            odometerKm = snapshot.odometerKm.toInt(),
                            fuelPercent = snapshot.fuelLevelPercent,
                            speedKmh = snapshot.speedKmh,
                            isSynchronized = true
                        )
                    } ?: DriverLiveTelemetry(isSynchronized = false),
                    engineLoadPercent = 0,
                    activeDtcAlert = activeAlert,
                    inspectedEcus = inspectedEcus,
                    dtcResolvedMessage = if (telemetry == null && alerts.isEmpty()) {
                        "No telemetry or DTC data is available for this vehicle yet."
                    } else null
                )
            }
        }
    }

    fun startDiagnosticScan() {
        refreshDiagnostics()
    }

    fun clearDtcCodes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isScanning = false,
                    dtcResolvedMessage = "Clearing DTC codes requires a linked OBD-II device write command. This action is not available from the mobile API yet."
                )
            }
        }
    }

    fun dismissResolvedMessage() {
        _uiState.update { it.copy(dtcResolvedMessage = null) }
    }
}
