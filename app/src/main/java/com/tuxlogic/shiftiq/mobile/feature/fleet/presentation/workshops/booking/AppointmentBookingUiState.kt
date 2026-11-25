package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem

data class BayOption(
    val id: String,
    val name: String,
    val type: String,
    val isAvailable: Boolean
)

data class ConfirmedAppointmentVoucher(
    val reservationCode: String,
    val workshopName: String,
    val district: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val bayName: String,
    val serviceTitle: String,
    val vehiclePlate: String,
    val estimatedPen: String
)

data class AlternativeBranch(
    val id: String,
    val name: String,
    val address: String,
    val activeBaysCount: Int
)

data class AppointmentBookingUiState(
    val workshopId: String = "",
    val workshopName: String = "",
    val workshopDistrict: String = "",
    val workshopAddress: String = "",
    val selectedVehicle: B2CVehicleItem? = null,
    val availableDays: List<String> = listOf("Hoy", "Mañana"),
    val selectedDayIndex: Int = 0,
    val availableBays: List<BayOption> = emptyList(),
    val selectedBayId: String = "",
    val availableSlots: List<String> = listOf("08:30 AM", "10:00 AM", "11:30 AM", "02:00 PM", "03:30 PM", "05:00 PM"),
    val selectedSlotIndex: Int = 1,
    val reasonNotes: String = "",
    val estimatedPricePen: String = "",
    val isBookingLoading: Boolean = false,
    val showConfirmationModal: Boolean = false,
    val successVoucher: ConfirmedAppointmentVoucher? = null,
    val showConflictErrorDialog: Boolean = false,
    val showBranchUnavailableDialog: Boolean = false,
    val alternativeBranches: List<AlternativeBranch> = emptyList(),
    val errorMessage: String? = null
)
