package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.WorkshopItem

enum class WorkshopFilter(val label: String) {
    ALL("Todos"),
    NEAR_ME("Cerca de mí (< 2 km)"),
    RATING_45_PLUS("Calificación 4.7+"),
    FREE_BAYS("Bahías Libres Hoy")
}

data class WorkshopsUiState(
    val searchQuery: String = "",
    val selectedFilter: WorkshopFilter = WorkshopFilter.ALL,
    val allWorkshops: List<WorkshopItem> = emptyList(),
    val filteredWorkshops: List<WorkshopItem> = emptyList(),
    val selectedVehicle: B2CVehicleItem? = null,
    val selectedWorkshopForBooking: WorkshopItem? = null,
    val selectedDate: String = "Hoy, 12 Nov",
    val selectedTimeSlot: String = "3:30 PM",
    val bookingReason: String = "Diagnóstico preventivo DTC P0135 (Sensor O2)",
    val isBookingLoading: Boolean = false,
    val bookingSuccessVoucher: BookingVoucher? = null,
    val errorMessage: String? = null
)

data class BookingVoucher(
    val appointmentCode: String,
    val workshopName: String,
    val district: String,
    val scheduledDateTime: String,
    val vehiclePlate: String,
    val bayNumber: String = "Bahía 2",
    val estimatedTotalPen: Double = 160.0
)
