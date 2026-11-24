package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchCatalogUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetAppointmentsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.UpdateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetVehicleByIdUseCase
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MyAppointmentsViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getAppointments: GetAppointmentsUseCase,
    private val updateAppointment: UpdateAppointmentUseCase,
    private val getBranchCatalog: GetBranchCatalogUseCase,
    private val getVehicleById: GetVehicleByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyAppointmentsUiState())
    val uiState: StateFlow<MyAppointmentsUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        _uiState.update { it.copy(isLoading = true, userMessage = null) }
        viewModelScope.launch {
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            if (userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(isLoading = false, userMessage = "Your session expired. Please sign in again.")
                }
                return@launch
            }

            val customer = when (val result = getCustomerByUserId(userId)) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, userMessage = result.error.message) }
                    return@launch
                }
            }

            val branchNames = when (val catalog = getBranchCatalog()) {
                is AppResult.Success -> catalog.data.associate {
                    it.branchId to it.displayName
                }
                is AppResult.Failure -> emptyMap()
            }

            when (val result = getAppointments(customerId = UUID.fromString(customer.id))) {
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, userMessage = result.error.message) }
                }

                is AppResult.Success -> {
                    val displayItems = result.data.map { appointment ->
                        toDisplayItem(appointment, branchNames[appointment.branchId.toString()].orEmpty())
                    }
                    val upcoming = displayItems.filter {
                        it.status.equals(AppointmentStatus.PENDING.name, ignoreCase = true)
                    }
                    val history = displayItems.filterNot {
                        it.status.equals(AppointmentStatus.PENDING.name, ignoreCase = true)
                    }
                    _uiState.update {
                        it.copy(
                            upcomingAppointments = upcoming,
                            historyAppointments = history,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun toDisplayItem(appointment: Appointment, workshopName: String): AppointmentDisplayItem {
        val vehicleResult = getVehicleById(VehicleId(appointment.vehicleId))
        val vehicleDescription = when (vehicleResult) {
            is AppResult.Success -> {
                val vehicle = vehicleResult.data
                "${vehicle.brand} ${vehicle.model} ${vehicle.year}"
            }
            is AppResult.Failure -> "Vehicle"
        }
        val vehiclePlate = when (vehicleResult) {
            is AppResult.Success -> vehicleResult.data.plateNumber.value
            is AppResult.Failure -> "—"
        }
        val (date, time) = formatSchedule(appointment.scheduledStart)
        return AppointmentDisplayItem(
            id = appointment.id,
            appointmentCode = appointment.id.toString().take(8).uppercase(),
            workshopName = workshopName.ifBlank { "Workshop" },
            address = "",
            scheduledDate = date,
            scheduledTime = time,
            bayNumber = "",
            vehiclePlate = vehiclePlate,
            vehicleDescription = vehicleDescription,
            reasonOrService = appointment.notes.orEmpty().ifBlank { "Scheduled service" },
            estimatedCostPen = "",
            status = appointment.status.name
        )
    }

    private fun formatSchedule(raw: String): Pair<String, String> {
        return runCatching {
            val dateTime = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val date = dateTime.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "PE")))
            val time = dateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))
            date to time
        }.getOrElse { raw to "" }
    }

    fun onTabSelected(tab: AppointmentTabFilter) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onRequestCancelAppointment(item: AppointmentDisplayItem) {
        _uiState.update { it.copy(appointmentToCancel = item) }
    }

    fun onDismissCancelDialog() {
        _uiState.update { it.copy(appointmentToCancel = null) }
    }

    fun confirmCancellation() {
        val item = _uiState.value.appointmentToCancel ?: return
        _uiState.update { it.copy(isCanceling = true) }

        viewModelScope.launch {
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            val customerId = userId?.let { (getCustomerByUserId(it) as? AppResult.Success)?.data?.id }
            if (customerId == null) {
                _uiState.update {
                    it.copy(isCanceling = false, userMessage = "Unable to cancel this appointment right now.")
                }
                return@launch
            }

            val appointments = (getAppointments(customerId = UUID.fromString(customerId)) as? AppResult.Success)
                ?.data
                .orEmpty()
            val appointment = appointments.firstOrNull { it.id == item.id }
            if (appointment == null) {
                _uiState.update {
                    it.copy(isCanceling = false, userMessage = "Appointment was not found on the server.")
                }
                return@launch
            }

            when (
                val result = updateAppointment(
                    appointmentId = appointment.id,
                    branchId = appointment.branchId,
                    customerId = appointment.customerId,
                    vehicleId = appointment.vehicleId,
                    scheduledStart = appointment.scheduledStart,
                    status = AppointmentStatus.CANCELED.name,
                    notes = appointment.notes
                )
            ) {
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isCanceling = false, userMessage = result.error.message)
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { current ->
                        val updatedCanceledItem = item.copy(status = AppointmentStatus.CANCELED.name)
                        current.copy(
                            isCanceling = false,
                            appointmentToCancel = null,
                            upcomingAppointments = current.upcomingAppointments.filterNot { it.id == item.id },
                            historyAppointments = listOf(updatedCanceledItem) + current.historyAppointments,
                            userMessage = "Appointment ${item.appointmentCode} was canceled."
                        )
                    }
                }
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
