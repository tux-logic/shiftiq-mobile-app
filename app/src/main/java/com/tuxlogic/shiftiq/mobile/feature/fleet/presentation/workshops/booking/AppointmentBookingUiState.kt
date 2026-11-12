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
    val workshopId: String = "w1",
    val workshopName: String = "Taller Automotriz Silva - Sede San Miguel",
    val workshopDistrict: String = "San Miguel",
    val workshopAddress: String = "Av. La Marina 2450 • San Miguel",
    val selectedVehicle: B2CVehicleItem? = null,
    val availableDays: List<String> = listOf("Hoy 15 Feb", "Mañana 16 Feb", "Jue 17 Feb", "Vie 18 Feb"),
    val selectedDayIndex: Int = 0,
    val availableBays: List<BayOption> = listOf(
        BayOption("bay-1", "Bahía 1", "Diagnóstico Rápido y Scanner CAN-BUS", true),
        BayOption("bay-2", "Bahía 2", "Elevador Hidráulico (Escape y Frenos)", true)
    ),
    val selectedBayId: String = "bay-2",
    val availableSlots: List<String> = listOf("08:30 AM", "10:00 AM", "11:30 AM", "02:00 PM", "03:30 PM", "05:00 PM"),
    val selectedSlotIndex: Int = 1,
    val reasonNotes: String = "Diagnóstico preventivo DTC P0135 (Sensor O2 Banco 1)",
    val estimatedPricePen: String = "S/ 140 - S/ 180 PEN",
    val isBookingLoading: Boolean = false,
    val showConfirmationModal: Boolean = false,
    val successVoucher: ConfirmedAppointmentVoucher? = null,
    val showConflictErrorDialog: Boolean = false,
    val showBranchUnavailableDialog: Boolean = false,
    val alternativeBranches: List<AlternativeBranch> = listOf(
        AlternativeBranch("b-surquillo", "Sede Central - Surquillo", "Av. Tomás Marsano 1240", 4),
        AlternativeBranch("b-olivos", "Sede Norte - Los Olivos", "Av. Alfredo Mendiola 3500", 3)
    )
)
