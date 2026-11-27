package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetAppointmentsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.CreateWorkOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.UUID
import javax.inject.Inject

data class DtcWorkOrderPrefill(
    val branchId: String,
    val vehicleId: String,
    val dtcCode: String,
    val plate: String,
    val model: String,
    val customerName: String,
    val diagnosticSummary: String
) : Serializable

data class CreateWorkOrderUiState(
    val isLoadingAppointments: Boolean = true,
    val isSubmitting: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val selectedAppointment: Appointment? = null,
    val dtcPrefill: DtcWorkOrderPrefill? = null,
    val mileage: String = "",
    val diagnosticSummary: String = "",
    val errorMessage: String? = null,
    val created: Boolean = false
)

@HiltViewModel
class CreateWorkOrderViewModel @Inject constructor(
    private val getAppointments: GetAppointmentsUseCase,
    private val createWorkOrder: CreateWorkOrderUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateWorkOrderUiState())
    val uiState: StateFlow<CreateWorkOrderUiState> = _uiState.asStateFlow()
    private var requestedAppointmentId: String? = null
    private var appointmentsLoadVersion: Long = 0

    init { loadAppointments() }

    fun applyAppointmentPrefill(appointmentId: String) {
        requestedAppointmentId = appointmentId
        loadAppointments()
    }

    fun loadAppointments() {
        val loadVersion = ++appointmentsLoadVersion
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAppointments = true, errorMessage = null) }
            val activeBranchId = sessionDataStore.activeBranchId.first()
            val prefill = _uiState.value.dtcPrefill
            val requestedId = requestedAppointmentId
            val requestedBranchId = prefill?.branchId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            val branchId = requestedBranchId?.let(::BranchId) ?: activeBranchId
            if (branchId == null) {
                if (loadVersion == appointmentsLoadVersion) {
                    _uiState.update { it.copy(isLoadingAppointments = false, errorMessage = "Selecciona una sede activa para continuar.") }
                }
                return@launch
            }
            when (val result = getAppointments(
                branchId = branchId.value,
                status = if (requestedId == null) "PENDING" else null
            )) {
                is AppResult.Success -> {
                    if (loadVersion != appointmentsLoadVersion) return@launch
                    _uiState.update { current ->
                    val activePrefill = current.dtcPrefill ?: prefill
                    val matchingAppointments = activePrefill?.vehicleId?.let { vehicleId ->
                        result.data.filter { it.vehicleId.toString() == vehicleId }
                    } ?: result.data
                    val prefilledAppointment = requestedId?.let { id ->
                        matchingAppointments.firstOrNull { it.id.toString() == id }
                    }
                    current.copy(
                        isLoadingAppointments = false,
                        appointments = matchingAppointments,
                        selectedAppointment = prefilledAppointment
                            ?: current.selectedAppointment
                            ?: if (activePrefill != null) matchingAppointments.firstOrNull() else null,
                        errorMessage = when {
                            requestedId != null && prefilledAppointment == null ->
                                "No se encontró la cita para abrir la orden."
                            activePrefill != null && matchingAppointments.isEmpty() ->
                            "No hay una cita pendiente asociada a este vehículo para abrir la orden."
                            else -> null
                        }
                    )
                    }
                }
                is AppResult.Failure -> if (loadVersion == appointmentsLoadVersion) {
                    _uiState.update { it.copy(isLoadingAppointments = false, errorMessage = result.error.message) }
                }
            }
        }
    }

    fun applyDtcPrefill(prefill: DtcWorkOrderPrefill) {
        _uiState.update { current ->
            val matchingAppointments = current.appointments.filter { it.vehicleId.toString() == prefill.vehicleId }
            current.copy(
                dtcPrefill = prefill,
                mileage = "35000",
                diagnosticSummary = prefill.diagnosticSummary,
                appointments = if (current.appointments.isEmpty()) current.appointments else matchingAppointments,
                selectedAppointment = current.selectedAppointment
                    ?: matchingAppointments.firstOrNull(),
                errorMessage = if (!current.isLoadingAppointments && matchingAppointments.isEmpty()) {
                    "No hay una cita pendiente asociada a este vehículo para abrir la orden."
                } else null
            )
        }
        loadAppointments()
    }

    fun selectAppointment(appointment: Appointment) =
        _uiState.update { it.copy(selectedAppointment = appointment, errorMessage = null) }

    fun updateMileage(value: String) = _uiState.update { it.copy(mileage = value.filter(Char::isDigit)) }

    fun updateDiagnostic(value: String) = _uiState.update { it.copy(diagnosticSummary = value) }

    fun submit() {
        val state = _uiState.value
        val appointment = state.selectedAppointment
        val mileage = state.mileage.toIntOrNull()
        when {
            appointment == null -> {
                _uiState.update { it.copy(errorMessage = "Selecciona una cita pendiente.") }
                return
            }
            mileage == null -> {
                _uiState.update { it.copy(errorMessage = "Ingresa el kilometraje de entrada.") }
                return
            }
            state.diagnosticSummary.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Describe el diagnóstico inicial.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            when (val result = createWorkOrder(
                appointmentId = appointment.id.toString(),
                branchId = BranchId(appointment.branchId),
                vehicleId = VehicleId(appointment.vehicleId),
                customerId = CustomerId(appointment.customerId),
                diagnosticSummary = state.diagnosticSummary,
                mileageIn = mileage
            )) {
                is AppResult.Success -> _uiState.update { it.copy(isSubmitting = false, created = true) }
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, errorMessage = result.error.message) }
            }
        }
    }
}
