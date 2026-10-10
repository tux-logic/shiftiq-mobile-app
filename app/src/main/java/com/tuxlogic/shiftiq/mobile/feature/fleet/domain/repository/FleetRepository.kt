package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import java.util.UUID

/**
 * Contrato de repositorio para el Bounded Context Fleet (Citas, Personal y Clientes vinculados).
 */
interface FleetRepository {

    // === Citas (Appointments) ===
    suspend fun getAppointments(
        branchId: UUID? = null,
        status: String? = null,
        customerId: UUID? = null,
        vehicleId: UUID? = null
    ): AppResult<List<Appointment>>

    suspend fun getAppointmentById(appointmentId: UUID): AppResult<Appointment>

    suspend fun createAppointment(
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        notes: String?
    ): AppResult<Appointment>

    suspend fun updateAppointment(
        appointmentId: UUID,
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        status: String,
        notes: String?
    ): AppResult<Appointment>

    suspend fun deleteAppointment(appointmentId: UUID): AppResult<Unit>

    // === Personal Técnico (Employee Registrations) ===
    suspend fun getEmployeeRegistrations(
        branchId: UUID? = null,
        status: String? = null,
        employeeId: UUID? = null
    ): AppResult<List<EmployeeRegistration>>

    suspend fun getEmployeeRegistrationById(id: UUID): AppResult<EmployeeRegistration>

    suspend fun createEmployeeRegistration(
        employeeId: UUID,
        branchId: UUID,
        speciality: String,
        specialityName: String?,
        salary: Double,
        role: String?
    ): AppResult<EmployeeRegistration>

    suspend fun requestEmployeeJoin(
        employeeId: UUID,
        branchId: UUID,
        speciality: String?,
        specialityName: String?,
        salary: Double?
    ): AppResult<EmployeeRegistration>

    suspend fun approveEmployeeRegistration(id: UUID): AppResult<EmployeeRegistration>

    suspend fun rejectEmployeeRegistration(id: UUID, reason: String?): AppResult<EmployeeRegistration>

    // === Clientes Vinculados (Customer Registrations) ===
    suspend fun getCustomerRegistrations(
        branchId: UUID? = null,
        status: String? = null,
        customerId: UUID? = null
    ): AppResult<List<CustomerRegistration>>

    suspend fun createCustomerRegistration(
        customerId: UUID,
        branchId: UUID
    ): AppResult<CustomerRegistration>
}
