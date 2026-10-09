package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model

import java.util.UUID

/**
 * Estado del ciclo de vida de una cita en el taller.
 */
enum class AppointmentStatus(val label: String) {
    PENDING("Pendiente"),
    COMPLETED("Completada"),
    CANCELED("Cancelada");

    companion object {
        fun fromString(value: String?): AppointmentStatus {
            return when (value?.uppercase()) {
                "COMPLETED" -> COMPLETED
                "CANCELED" -> CANCELED
                else -> PENDING
            }
        }
    }
}

/**
 * Entidad de dominio que representa una cita agendada en una sede del taller.
 */
data class Appointment(
    val id: UUID,
    val branchId: UUID,
    val customerId: UUID,
    val vehicleId: UUID,
    val scheduledStart: String,
    val status: AppointmentStatus,
    val notes: String?
)

/**
 * Entidad de dominio para la vinculación o postulación de un empleado técnico a una sede.
 */
data class EmployeeRegistration(
    val id: UUID,
    val employeeId: UUID,
    val branchId: UUID,
    val speciality: String,
    val specialityName: String?,
    val salary: Double,
    val role: String?,
    val status: String
) {
    val isPending: Boolean get() = status.equals("PENDING_APPROVAL", ignoreCase = true)
    val isActive: Boolean get() = status.equals("ACTIVE", ignoreCase = true)
}

/**
 * Entidad de dominio para la vinculación de un cliente a una sede del taller.
 */
data class CustomerRegistration(
    val id: UUID,
    val customerId: UUID,
    val branchId: UUID,
    val status: String
)
