package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.CreateAppointmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class CreateAppointmentUiState(
    val branchId: String = "",
    val customerId: String = "",
    val vehicleId: String = "",
    val scheduledStart: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CreateAppointmentViewModel @Inject constructor(
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAppointmentUiState())
    val uiState: StateFlow<CreateAppointmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val activeBranch = sessionDataStore.activeBranchId.firstOrNull()?.value
            val defaultTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

            _uiState.update {
                it.copy(
                    branchId = activeBranch?.toString() ?: "",
                    scheduledStart = defaultTime
                )
            }
        }
    }

    fun onBranchIdChanged(value: String) {
        _uiState.update { it.copy(branchId = value, errorMessage = null) }
    }

    fun onCustomerIdChanged(value: String) {
        _uiState.update { it.copy(customerId = value, errorMessage = null) }
    }

    fun onVehicleIdChanged(value: String) {
        _uiState.update { it.copy(vehicleId = value, errorMessage = null) }
    }

    fun onScheduledStartChanged(value: String) {
        _uiState.update { it.copy(scheduledStart = value, errorMessage = null) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun createAppointment() {
        val currentState = _uiState.value

        val branchUuid = runCatching { UUID.fromString(currentState.branchId.trim()) }.getOrNull()
        if (branchUuid == null) {
            _uiState.update { it.copy(errorMessage = "El ID de sede debe ser un UUID válido.") }
            return
        }

        val customerUuid = runCatching { UUID.fromString(currentState.customerId.trim()) }.getOrNull()
        if (customerUuid == null) {
            _uiState.update { it.copy(errorMessage = "El ID de cliente debe ser un UUID válido.") }
            return
        }

        val vehicleUuid = runCatching { UUID.fromString(currentState.vehicleId.trim()) }.getOrNull()
        if (vehicleUuid == null) {
            _uiState.update { it.copy(errorMessage = "El ID de vehículo debe ser un UUID válido.") }
            return
        }

        if (currentState.scheduledStart.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Indique la fecha y hora de la cita.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            createAppointmentUseCase(
                branchId = branchUuid,
                customerId = customerUuid,
                vehicleId = vehicleUuid,
                scheduledStart = currentState.scheduledStart.trim(),
                notes = currentState.notes.takeIf { it.isNotBlank() }
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}
