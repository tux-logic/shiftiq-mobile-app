package com.tuxlogic.shiftiq.mobile.feature.iot.presentation.telemetry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TelemetryDashboardUiState(
    val branchId: String? = null,
    val activeBranchName: String = "Sede Principal",
    val allAlerts: List<DtcAlert> = emptyList(),
    val selectedStatus: DtcStatus? = DtcStatus.ACTIVE,
    val selectedSeverity: DtcSeverity? = null,
    val selectedAlert: DtcAlert? = null,
    val sensorSnapshot: TelemetrySnapshot? = null,
    val isLoading: Boolean = false,
    val isLoadingSensors: Boolean = false,
    val errorMessage: String? = null
) {
    val visibleAlerts: List<DtcAlert>
        get() = allAlerts.filter { (selectedStatus == null || it.status == selectedStatus) && (selectedSeverity == null || it.severity == selectedSeverity) }
    val activeCount: Int get() = allAlerts.count { it.status == DtcStatus.ACTIVE }
    val criticalCount: Int get() = allAlerts.count { it.status == DtcStatus.ACTIVE && it.severity == DtcSeverity.CRITICAL }
    val moderateCount: Int get() = allAlerts.count { it.status == DtcStatus.ACTIVE && it.severity == DtcSeverity.MEDIUM }
}

@HiltViewModel
class TelemetryDashboardViewModel @Inject constructor(
    private val repository: IotRepository,
    private val sessionDataStore: SessionDataStore,
    private val getBranchById: GetBranchByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(TelemetryDashboardUiState())
    val uiState: StateFlow<TelemetryDashboardUiState> = _uiState.asStateFlow()

    init { loadAlerts() }

    fun loadAlerts() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val branchId = sessionDataStore.activeBranchId.first()
        if (branchId == null) {
            _uiState.update { it.copy(branchId = null, activeBranchName = "Sede Principal", allAlerts = emptyList(), isLoading = false, errorMessage = "Selecciona una sede activa para consultar la telemetría.") }
            return@launch
        }
        val branchName = (getBranchById(branchId.toString()) as? AppResult.Success)?.data?.name
            ?.takeIf(String::isNotBlank) ?: "Sede Principal"
        _uiState.update { it.copy(branchId = branchId.toString(), activeBranchName = branchName) }
        when (val registrations = repository.getRegistrations(branchId, RegistrationStatus.ACTIVE)) {
            is AppResult.Failure -> _uiState.update { it.copy(isLoading = false, errorMessage = registrations.error.message) }
            is AppResult.Success -> {
                val alerts = mutableListOf<DtcAlert>()
                var firstError: String? = null
                registrations.data.forEach { registration ->
                    when (val result = repository.getRegistrationDtcAlerts(registration.id)) {
                        is AppResult.Success -> {
                            if (result.data.isEmpty()) return@forEach

                            val vehicle = (repository.getVehicleById(registration.vehicleId) as? AppResult.Success)?.data
                            val snapshots = (repository.getRegistrationTelemetryHistory(
                                registration.id,
                                page = 0,
                                size = 100
                            ) as? AppResult.Success)?.data.orEmpty().associateBy { it.id }

                            alerts += result.data.map { alert ->
                                val snapshot = snapshots[alert.telemetrySnapshotId]
                                alert.copy(
                                    branchId = registration.branchId,
                                    vehicleId = registration.vehicleId.toString(),
                                    vehiclePlate = vehicle?.plateNumber?.value
                                        ?: alert.vehiclePlate,
                                    vehicleModel = vehicle?.let { "${it.brand} ${it.model} ${it.year}".trim() }
                                        ?.takeIf(String::isNotBlank)
                                        ?: alert.vehicleModel,
                                    freezeFrame = snapshot?.let {
                                        mapOf(
                                            "RPM del motor" to "${it.rpm} rpm",
                                            "Temperatura" to "${it.temperature} °C",
                                            "Velocidad" to "${it.speedKmh} km/h",
                                            "Combustible" to "${it.fuelLevelPercent}%",
                                            "Odómetro" to "${it.odometerKm} km"
                                        )
                                    } ?: alert.freezeFrame
                                )
                            }
                        }
                        is AppResult.Failure -> if (firstError == null) firstError = result.error.message
                    }
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allAlerts = alerts.distinctBy(DtcAlert::id).sortedByDescending(DtcAlert::createdAt),
                        errorMessage = if (alerts.isEmpty() && firstError != null) firstError else null
                    )
                }
            }
        }
    }

    fun selectStatus(status: DtcStatus?) = _uiState.update { it.copy(selectedStatus = status) }
    fun selectSeverity(severity: DtcSeverity?) = _uiState.update { it.copy(selectedSeverity = severity) }
    fun selectAlert(alert: DtcAlert?) = _uiState.update { it.copy(selectedAlert = alert, sensorSnapshot = null) }

    fun markAlertAsResolved() = _uiState.update {
        it.copy(
            selectedAlert = null,
            errorMessage = "No se pudo actualizar el estado de la alerta: el backend todavía no permite esta operación."
        )
    }

    fun loadSensors(alert: DtcAlert) = viewModelScope.launch {
        val vehicleId = alert.vehicleId
        if (vehicleId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "El backend no devolvió el identificador del vehículo para esta alerta.") }
            return@launch
        }
        _uiState.update { it.copy(isLoadingSensors = true, errorMessage = null) }
        when (val result = repository.getVehicleTelemetryHistory(VehicleId(vehicleId), page = 0, size = 20)) {
            is AppResult.Success -> _uiState.update {
                it.copy(isLoadingSensors = false, sensorSnapshot = result.data.maxByOrNull(TelemetrySnapshot::createdAt))
            }
            is AppResult.Failure -> _uiState.update { it.copy(isLoadingSensors = false, errorMessage = result.error.message) }
        }
    }
}
