package com.tuxlogic.shiftiq.mobile.feature.fleet.data.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.api.FleetApiService
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CreateAppointmentRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CreateCustomerRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.CreateEmployeeRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.RejectEmployeeRegistrationRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.RequestEmployeeJoinRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.data.remote.dto.UpdateAppointmentRequestDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class FleetRepositoryImpl @Inject constructor(
    private val fleetApiService: FleetApiService,
    private val dispatchers: DispatcherProvider
) : FleetRepository {

    override suspend fun getAppointments(
        branchId: UUID?,
        status: String?,
        customerId: UUID?,
        vehicleId: UUID?
    ): AppResult<List<Appointment>> = withContext(dispatchers.io) {
        safeApiCall {
            fleetApiService.getAppointments(
                branchId = branchId?.toString(),
                status = status,
                customerId = customerId?.toString(),
                vehicleId = vehicleId?.toString()
            )
        }.map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override suspend fun getAppointmentById(appointmentId: UUID): AppResult<Appointment> =
        withContext(dispatchers.io) {
            safeApiCall { fleetApiService.getAppointmentById(appointmentId.toString()) }
                .map { it.toDomain() }
        }

    override suspend fun createAppointment(
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        notes: String?
    ): AppResult<Appointment> = withContext(dispatchers.io) {
        val request = CreateAppointmentRequestDto(
            branchId = branchId.toString(),
            customerId = customerId.toString(),
            vehicleId = vehicleId.toString(),
            scheduledStart = scheduledStart,
            notes = notes
        )
        safeApiCall { fleetApiService.createAppointment(request) }
            .map { it.toDomain() }
    }

    override suspend fun updateAppointment(
        appointmentId: UUID,
        branchId: UUID,
        customerId: UUID,
        vehicleId: UUID,
        scheduledStart: String,
        status: String,
        notes: String?
    ): AppResult<Appointment> = withContext(dispatchers.io) {
        val request = UpdateAppointmentRequestDto(
            branchId = branchId.toString(),
            customerId = customerId.toString(),
            vehicleId = vehicleId.toString(),
            scheduledStart = scheduledStart,
            status = status,
            notes = notes
        )
        safeApiCall { fleetApiService.updateAppointment(appointmentId.toString(), request) }
            .map { it.toDomain() }
    }

    override suspend fun deleteAppointment(appointmentId: UUID): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall { fleetApiService.deleteAppointment(appointmentId.toString()) }
        }

    override suspend fun getEmployeeRegistrations(
        branchId: UUID?,
        status: String?,
        employeeId: UUID?
    ): AppResult<List<EmployeeRegistration>> = withContext(dispatchers.io) {
        safeApiCall {
            fleetApiService.getEmployeeRegistrations(
                branchId = branchId?.toString(),
                status = status,
                employeeId = employeeId?.toString()
            )
        }.map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override suspend fun getEmployeeRegistrationById(id: UUID): AppResult<EmployeeRegistration> =
        withContext(dispatchers.io) {
            safeApiCall { fleetApiService.getEmployeeRegistrationById(id.toString()) }
                .map { it.toDomain() }
        }

    override suspend fun createEmployeeRegistration(
        employeeId: UUID,
        branchId: UUID,
        speciality: String,
        specialityName: String?,
        salary: Double,
        role: String?
    ): AppResult<EmployeeRegistration> = withContext(dispatchers.io) {
        val request = CreateEmployeeRegistrationRequestDto(
            employeeId = employeeId.toString(),
            branchId = branchId.toString(),
            speciality = speciality,
            specialityName = specialityName,
            salary = salary,
            role = role
        )
        safeApiCall { fleetApiService.createEmployeeRegistration(request) }
            .map { it.toDomain() }
    }

    override suspend fun requestEmployeeJoin(
        employeeId: UUID,
        branchId: UUID,
        speciality: String?,
        specialityName: String?,
        salary: Double?
    ): AppResult<EmployeeRegistration> = withContext(dispatchers.io) {
        val request = RequestEmployeeJoinRequestDto(
            employeeId = employeeId.toString(),
            branchId = branchId.toString(),
            speciality = speciality,
            specialityName = specialityName,
            salary = salary
        )
        safeApiCall { fleetApiService.requestEmployeeJoin(request) }
            .map { it.toDomain() }
    }

    override suspend fun approveEmployeeRegistration(id: UUID): AppResult<EmployeeRegistration> =
        withContext(dispatchers.io) {
            safeApiCall { fleetApiService.approveEmployeeRegistration(id.toString()) }
                .map { it.toDomain() }
        }

    override suspend fun rejectEmployeeRegistration(
        id: UUID,
        reason: String?
    ): AppResult<EmployeeRegistration> = withContext(dispatchers.io) {
        val request = RejectEmployeeRegistrationRequestDto(reason = reason)
        safeApiCall { fleetApiService.rejectEmployeeRegistration(id.toString(), request) }
            .map { it.toDomain() }
    }

    override suspend fun getCustomerRegistrations(
        branchId: UUID?,
        status: String?,
        customerId: UUID?
    ): AppResult<List<CustomerRegistration>> = withContext(dispatchers.io) {
        safeApiCall {
            fleetApiService.getCustomerRegistrations(
                branchId = branchId?.toString(),
                status = status,
                customerId = customerId?.toString()
            )
        }.map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override suspend fun createCustomerRegistration(
        customerId: UUID,
        branchId: UUID
    ): AppResult<CustomerRegistration> = withContext(dispatchers.io) {
        val request = CreateCustomerRegistrationRequestDto(
            customerId = customerId.toString(),
            branchId = branchId.toString()
        )
        safeApiCall { fleetApiService.createCustomerRegistration(request) }
            .map { it.toDomain() }
    }
}
