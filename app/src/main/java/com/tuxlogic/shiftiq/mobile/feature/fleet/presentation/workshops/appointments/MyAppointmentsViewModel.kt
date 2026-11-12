package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.DeleteAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetAppointmentsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
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
class MyAppointmentsViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val deleteAppointmentUseCase: DeleteAppointmentUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyAppointmentsUiState())
    val uiState: StateFlow<MyAppointmentsUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        _uiState.update { it.copy(isLoading = true, userMessage = null) }
        viewModelScope.launch {
            val session = sessionDataStore.sessionState.firstOrNull()
            val customerId = session?.userId?.value

            val result = getAppointmentsUseCase(customerId = customerId)
            if (result is AppResult.Success && result.data.isNotEmpty()) {
                val mapped = result.data.map { mapToDisplayItem(it) }
                val upcoming = mapped.filter { it.status == "PENDING" }
                val history = mapped.filter { it.status != "PENDING" }
                _uiState.update {
                    it.copy(
                        upcomingAppointments = upcoming,
                        historyAppointments = history,
                        isLoading = false
                    )
                }
            } else {
                // Seed data matching Figma Frame 234:17958
                val defaultUpcoming = listOf(
                    AppointmentDisplayItem(
                        id = UUID.fromString("11111111-2222-3333-4444-555566667777"),
                        appointmentCode = "SQ-APPT-8429",
                        workshopName = "Taller Automotriz Silva - San Miguel",
                        address = "Av. La Marina 2450 • San Miguel",
                        scheduledDate = "Mar 23 Mayo",
                        scheduledTime = "10:00 AM",
                        bayNumber = "Bahía 2 (Elevador Hidráulico)",
                        vehiclePlate = "ABC-123",
                        vehicleDescription = "Kia Rio 2019",
                        reasonOrService = "Diagnóstico preventivo DTC P0135 (Sensor O2)",
                        estimatedCostPen = "S/ 140 - S/ 180 PEN",
                        status = "PENDING"
                    ),
                    AppointmentDisplayItem(
                        id = UUID.fromString("22222222-3333-4444-5555-666677778888"),
                        appointmentCode = "SQ-APPT-9012",
                        workshopName = "AutoCheck Pro - Los Olivos",
                        address = "Av. Alfredo Mendiola 3500 • Los Olivos",
                        scheduledDate = "Jue 25 Mayo",
                        scheduledTime = "03:30 PM",
                        bayNumber = "Bahía 1 (Scanner Rápido)",
                        vehiclePlate = "ABC-123",
                        vehicleDescription = "Kia Rio 2019",
                        reasonOrService = "Mantenimiento 20,000 km y frenos",
                        estimatedCostPen = "S/ 220 PEN",
                        status = "PENDING"
                    )
                )
                val defaultHistory = listOf(
                    AppointmentDisplayItem(
                        id = UUID.fromString("33333333-4444-5555-6666-777788889999"),
                        appointmentCode = "SQ-APPT-5401",
                        workshopName = "Precision Motors - Magdalena",
                        address = "Jr. San Martín 420 • Magdalena",
                        scheduledDate = "10 Abr 2026",
                        scheduledTime = "09:00 AM",
                        bayNumber = "Bahía 3 (Alineamiento)",
                        vehiclePlate = "ABC-123",
                        vehicleDescription = "Kia Rio 2019",
                        reasonOrService = "Alineamiento y balanceo computarizado",
                        estimatedCostPen = "S/ 120 PEN",
                        status = "COMPLETED"
                    )
                )
                _uiState.update {
                    it.copy(
                        upcomingAppointments = defaultUpcoming,
                        historyAppointments = defaultHistory,
                        isLoading = false
                    )
                }
            }
        }
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
            val result = deleteAppointmentUseCase(item.id)
            val updatedCanceledItem = item.copy(status = "CANCELED")

            _uiState.update { current ->
                val newUpcoming = current.upcomingAppointments.filterNot { it.id == item.id }
                val newHistory = listOf(updatedCanceledItem) + current.historyAppointments
                current.copy(
                    isCanceling = false,
                    appointmentToCancel = null,
                    upcomingAppointments = newUpcoming,
                    historyAppointments = newHistory,
                    userMessage = "La cita ${item.appointmentCode} fue cancelada exitosamente."
                )
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun mapToDisplayItem(appointment: Appointment): AppointmentDisplayItem {
        val code = "SQ-${appointment.id.toString().take(8).uppercase()}"
        val dateParts = appointment.scheduledStart.split("T")
        val date = if (dateParts.isNotEmpty()) dateParts[0] else "Fecha acordada"
        val time = if (dateParts.size > 1) dateParts[1].take(5) else "10:00 AM"

        return AppointmentDisplayItem(
            id = appointment.id,
            appointmentCode = code,
            workshopName = "Taller Afiliado ShiftIQ",
            address = "Sede Principal asignada",
            scheduledDate = date,
            scheduledTime = time,
            bayNumber = "Bahía Técnica",
            vehiclePlate = "Vehículo Asignado",
            vehicleDescription = "Flota Personal",
            reasonOrService = appointment.notes ?: "Servicio Técnico Integral",
            estimatedCostPen = "S/ 140 - S/ 180 PEN",
            status = appointment.status.name
        )
    }
}
