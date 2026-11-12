package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments

import java.util.UUID

enum class AppointmentTabFilter {
    UPCOMING,
    HISTORY
}

data class AppointmentDisplayItem(
    val id: UUID,
    val appointmentCode: String,
    val workshopName: String,
    val address: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val bayNumber: String,
    val vehiclePlate: String,
    val vehicleDescription: String,
    val reasonOrService: String,
    val estimatedCostPen: String,
    val status: String // "PENDING", "COMPLETED", "CANCELED"
)

data class MyAppointmentsUiState(
    val selectedTab: AppointmentTabFilter = AppointmentTabFilter.UPCOMING,
    val upcomingAppointments: List<AppointmentDisplayItem> = emptyList(),
    val historyAppointments: List<AppointmentDisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val isCanceling: Boolean = false,
    val appointmentToCancel: AppointmentDisplayItem? = null,
    val userMessage: String? = null
)
