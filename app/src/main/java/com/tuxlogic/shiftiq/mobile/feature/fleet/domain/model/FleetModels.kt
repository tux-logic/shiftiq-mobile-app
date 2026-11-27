package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model

import java.util.UUID

enum class AppointmentStatus(val label: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELED("Canceled");

    companion object {
        fun fromString(value: String?): AppointmentStatus {
            return when (value?.uppercase()) {
                "CONFIRMED" -> CONFIRMED
                "COMPLETED" -> COMPLETED
                "CANCELED" -> CANCELED
                else -> PENDING
            }
        }
    }
}

data class Appointment(
    val id: UUID,
    val branchId: UUID,
    val customerId: UUID,
    val customerName: String = "Cliente del Taller",
    val customerPhone: String = "",
    val vehicleId: UUID,
    val vehiclePlate: String = "S/P",
    val vehicleModel: String = "Vehículo General",
    val bayId: UUID? = null,
    val scheduledStart: String,
    val scheduledEnd: String? = null,
    val serviceType: String = "Mantenimiento General",
    val status: AppointmentStatus,
    val notes: String? = null,
    val bayNumber: Int = 1,
    val createdAt: String? = null
)

data class BranchAvailability(
    val branchId: String,
    val date: String,
    val activeBaysCount: Int,
    val freeBaysNow: Int,
    val slots: List<BranchAvailabilitySlot>
)

data class BranchAvailabilitySlot(
    val start: String,
    val end: String,
    val availableBayIds: List<String>
)

data class EmployeeRegistration(
    val id: UUID,
    val employeeId: UUID,
    val branchId: UUID,
    val speciality: String,
    val specialityName: String? = null,
    val salary: Double,
    val role: String? = null,
    val status: String
) {
    val isPending: Boolean get() = status.equals("PENDING_APPROVAL", ignoreCase = true)
    val isActive: Boolean get() = status.equals("ACTIVE", ignoreCase = true)
}

data class StaffCareerHistory(
    val id: String,
    val employeeId: UUID,
    val branchId: UUID,
    val speciality: String,
    val specialityName: String? = null,
    val salary: Double,
    val role: String? = null,
    val status: String,
    val changedAt: String,
    val changeReason: String? = null
)

data class CustomerRegistration(
    val id: UUID,
    val customerId: UUID,
    val branchId: UUID,
    val status: String
) {
    val isActive: Boolean get() = status.equals("ACTIVE", ignoreCase = true)
}
