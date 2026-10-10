package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.DeleteAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.GetAppointmentsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.UpdateAppointmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AppointmentListUiState(
    val isLoading: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val filteredAppointments: List<Appointment> = emptyList(),
    val selectedStatusFilter: AppointmentStatus? = null,
    val activeBranchId: UUID? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AppointmentListViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val deleteAppointmentUseCase: DeleteAppointmentUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentListUiState())
    val uiState: StateFlow<AppointmentListUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val branchId = sessionDataStore.activeBranchId.firstOrNull()?.value
            _uiState.update { it.copy(activeBranchId = branchId) }

            getAppointmentsUseCase(branchId = branchId)
                .onSuccess { list ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            appointments = list,
                            filteredAppointments = filterList(list, state.selectedStatusFilter)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun setStatusFilter(status: AppointmentStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedStatusFilter = status,
                filteredAppointments = filterList(state.appointments, status)
            )
        }
    }

    fun markAppointmentStatus(appointment: Appointment, newStatus: AppointmentStatus) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updateAppointmentUseCase(
                appointmentId = appointment.id,
                branchId = appointment.branchId,
                customerId = appointment.customerId,
                vehicleId = appointment.vehicleId,
                scheduledStart = appointment.scheduledStart,
                status = newStatus.name,
                notes = appointment.notes
            ).onSuccess { updated ->
                _uiState.update { state ->
                    val updatedList = state.appointments.map { if (it.id == updated.id) updated else it }
                    state.copy(
                        isLoading = false,
                        appointments = updatedList,
                        filteredAppointments = filterList(updatedList, state.selectedStatusFilter),
                        successMessage = "Cita actualizada a ${newStatus.label}"
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun deleteAppointment(appointmentId: UUID) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deleteAppointmentUseCase(appointmentId)
                .onSuccess {
                    _uiState.update { state ->
                        val updatedList = state.appointments.filterNot { it.id == appointmentId }
                        state.copy(
                            isLoading = false,
                            appointments = updatedList,
                            filteredAppointments = filterList(updatedList, state.selectedStatusFilter),
                            successMessage = "Cita eliminada correctamente"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun filterList(
        list: List<Appointment>,
        status: AppointmentStatus?
    ): List<Appointment> {
        return if (status == null) list else list.filter { it.status == status }
    }
}
