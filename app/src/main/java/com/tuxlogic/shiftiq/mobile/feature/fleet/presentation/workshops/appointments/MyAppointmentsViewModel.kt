package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MyAppointmentsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MyAppointmentsUiState())
    val uiState: StateFlow<MyAppointmentsUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        _uiState.update { it.copy(isLoading = true, userMessage = null) }
        viewModelScope.launch {
            delay(250)
            // Citas de demostración locales; no se consultan ni modifican en el backend.
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
            delay(250)
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

}
