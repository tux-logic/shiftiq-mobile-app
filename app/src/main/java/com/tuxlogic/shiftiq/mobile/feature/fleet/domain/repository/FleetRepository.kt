package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.StaffCareerHistory
import java.util.UUID

interface FleetRepository {

    // === Appointments ===

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
        notes: String? = null
    ): AppResult<Appointment>

    suspend fun updateAppointment(
        appointmentId: UUID,
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        status: String,
        notes: String? = null
    ): AppResult<Appointment>

    suspend fun deleteAppointment(appointmentId: UUID): AppResult<Unit>

    // === Technical Staff (Employee Registrations) ===

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

    suspend fun updateEmployeeRegistration(
        id: UUID,
        speciality: String,
        specialityName: String?,
        salary: Double
    ): AppResult<EmployeeRegistration>

    suspend fun deleteEmployeeRegistration(id: UUID): AppResult<Unit>

    suspend fun getStaffCareerHistory(branchId: UUID, employeeId: UUID): AppResult<List<StaffCareerHistory>>

    // === Linked Customers (Customer Registrations) ===

    suspend fun getCustomerRegistrations(
        branchId: UUID? = null,
        status: String? = null,
        customerId: UUID? = null
    ): AppResult<List<CustomerRegistration>>

    suspend fun getCustomerRegistrationById(id: UUID): AppResult<CustomerRegistration>

    suspend fun createCustomerRegistration(
        customerId: UUID,
        branchId: UUID
    ): AppResult<CustomerRegistration>

    suspend fun updateCustomerRegistration(
        id: UUID,
        status: String
    ): AppResult<CustomerRegistration>

    suspend fun deleteCustomerRegistration(id: UUID): AppResult<Unit>
}
