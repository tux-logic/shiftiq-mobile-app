package com.tuxlogic.shiftiq.mobile.feature.fleet

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.CustomerRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.StaffCareerHistory
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.ApproveEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateCustomerRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.DeleteCustomerRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.DeleteEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetAppointmentsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetCustomerRegistrationByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetCustomerRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetEmployeeRegistrationByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetEmployeeRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetStaffCareerHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.RejectEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.RequestEmployeeJoinUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.UpdateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.UpdateCustomerRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.UpdateEmployeeRegistrationUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class FleetUseCasesTest {

    private val fleetRepository: FleetRepository = mockk()

    private lateinit var getAppointmentsUseCase: GetAppointmentsUseCase
    private lateinit var createAppointmentUseCase: CreateAppointmentUseCase
    private lateinit var updateAppointmentUseCase: UpdateAppointmentUseCase

    private lateinit var getEmployeeRegistrationsUseCase: GetEmployeeRegistrationsUseCase
    private lateinit var getEmployeeRegistrationByIdUseCase: GetEmployeeRegistrationByIdUseCase
    private lateinit var createEmployeeRegistrationUseCase: CreateEmployeeRegistrationUseCase
    private lateinit var requestEmployeeJoinUseCase: RequestEmployeeJoinUseCase
    private lateinit var approveEmployeeRegistrationUseCase: ApproveEmployeeRegistrationUseCase
    private lateinit var rejectEmployeeRegistrationUseCase: RejectEmployeeRegistrationUseCase
    private lateinit var updateEmployeeRegistrationUseCase: UpdateEmployeeRegistrationUseCase
    private lateinit var deleteEmployeeRegistrationUseCase: DeleteEmployeeRegistrationUseCase
    private lateinit var getStaffCareerHistoryUseCase: GetStaffCareerHistoryUseCase

    private lateinit var getCustomerRegistrationsUseCase: GetCustomerRegistrationsUseCase
    private lateinit var getCustomerRegistrationByIdUseCase: GetCustomerRegistrationByIdUseCase
    private lateinit var createCustomerRegistrationUseCase: CreateCustomerRegistrationUseCase
    private lateinit var updateCustomerRegistrationUseCase: UpdateCustomerRegistrationUseCase
    private lateinit var deleteCustomerRegistrationUseCase: DeleteCustomerRegistrationUseCase

    @Before
    fun setUp() {
        getAppointmentsUseCase = GetAppointmentsUseCase(fleetRepository)
        createAppointmentUseCase = CreateAppointmentUseCase(fleetRepository)
        updateAppointmentUseCase = UpdateAppointmentUseCase(fleetRepository)

        getEmployeeRegistrationsUseCase = GetEmployeeRegistrationsUseCase(fleetRepository)
        getEmployeeRegistrationByIdUseCase = GetEmployeeRegistrationByIdUseCase(fleetRepository)
        createEmployeeRegistrationUseCase = CreateEmployeeRegistrationUseCase(fleetRepository)
        requestEmployeeJoinUseCase = RequestEmployeeJoinUseCase(fleetRepository)
        approveEmployeeRegistrationUseCase = ApproveEmployeeRegistrationUseCase(fleetRepository)
        rejectEmployeeRegistrationUseCase = RejectEmployeeRegistrationUseCase(fleetRepository)
        updateEmployeeRegistrationUseCase = UpdateEmployeeRegistrationUseCase(fleetRepository)
        deleteEmployeeRegistrationUseCase = DeleteEmployeeRegistrationUseCase(fleetRepository)
        getStaffCareerHistoryUseCase = GetStaffCareerHistoryUseCase(fleetRepository)

        getCustomerRegistrationsUseCase = GetCustomerRegistrationsUseCase(fleetRepository)
        getCustomerRegistrationByIdUseCase = GetCustomerRegistrationByIdUseCase(fleetRepository)
        createCustomerRegistrationUseCase = CreateCustomerRegistrationUseCase(fleetRepository)
        updateCustomerRegistrationUseCase = UpdateCustomerRegistrationUseCase(fleetRepository)
        deleteCustomerRegistrationUseCase = DeleteCustomerRegistrationUseCase(fleetRepository)
    }

    // === Appointments ===

    @Test
    fun `getAppointments returns list of appointments from repository`() = runTest {
        val branchId = UUID.randomUUID()
        val mockList = listOf(
            Appointment(
                id = UUID.randomUUID(),
                branchId = branchId,
                customerId = UUID.randomUUID(),
                vehicleId = UUID.randomUUID(),
                scheduledStart = "2026-10-15T10:00:00Z",
                status = AppointmentStatus.PENDING,
                notes = "Mantenimiento preventivo"
            )
        )

        coEvery { fleetRepository.getAppointments(branchId = branchId) } returns AppResult.Success(mockList)

        val result = getAppointmentsUseCase(branchId = branchId)

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(1, data.size)
        assertEquals(AppointmentStatus.PENDING, data.first().status)
        coVerify(exactly = 1) { fleetRepository.getAppointments(branchId = branchId) }
    }

    @Test
    fun `createAppointment delegates to repository and returns created appointment`() = runTest {
        val branchId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()
        val scheduledStart = "2026-10-20T14:00:00Z"
        val notes = "Cambio de aceite sintético"

        val expected = Appointment(
            id = UUID.randomUUID(),
            branchId = branchId,
            customerId = customerId,
            vehicleId = vehicleId,
            scheduledStart = scheduledStart,
            status = AppointmentStatus.PENDING,
            notes = notes
        )

        coEvery {
            fleetRepository.createAppointment(branchId, customerId, vehicleId, scheduledStart, notes)
        } returns AppResult.Success(expected)

        val result = createAppointmentUseCase(branchId, customerId, vehicleId, scheduledStart, notes)

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(expected.id, data.id)
        assertEquals(notes, data.notes)
        coVerify(exactly = 1) {
            fleetRepository.createAppointment(branchId, customerId, vehicleId, scheduledStart, notes)
        }
    }

    @Test
    fun `updateAppointment changes status to COMPLETED`() = runTest {
        val appointmentId = UUID.randomUUID()
        val branchId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()
        val scheduledStart = "2026-10-20T14:00:00Z"

        val updated = Appointment(
            id = appointmentId,
            branchId = branchId,
            customerId = customerId,
            vehicleId = vehicleId,
            scheduledStart = scheduledStart,
            status = AppointmentStatus.COMPLETED,
            notes = null
        )

        coEvery {
            fleetRepository.updateAppointment(
                appointmentId,
                branchId,
                customerId,
                vehicleId,
                scheduledStart,
                "COMPLETED",
                null
            )
        } returns AppResult.Success(updated)

        val result = updateAppointmentUseCase(
            appointmentId,
            branchId,
            customerId,
            vehicleId,
            scheduledStart,
            "COMPLETED",
            null
        )

        assertTrue(result is AppResult.Success)
        assertEquals(AppointmentStatus.COMPLETED, (result as AppResult.Success).data.status)
    }

    // === Staff Registrations ===

    @Test
    fun `createEmployeeRegistration with negative salary returns validation error`() = runTest {
        val result = createEmployeeRegistrationUseCase(
            employeeId = UUID.randomUUID(),
            branchId = UUID.randomUUID(),
            speciality = "GENERAL_MECHANIC",
            specialityName = "Mecanica General",
            salary = -100.0,
            role = "ROLE_EMPLOYEE"
        )
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
    }

    @Test
    fun `updateEmployeeRegistration with valid terms calls repository`() = runTest {
        val id = UUID.randomUUID()
        val expected = EmployeeRegistration(
            id = id,
            employeeId = UUID.randomUUID(),
            branchId = UUID.randomUUID(),
            speciality = "DIAGNOSTIC",
            specialityName = "Diagnostico",
            salary = 4000.0,
            role = "ROLE_EMPLOYEE",
            status = "ACTIVE"
        )
        coEvery { fleetRepository.updateEmployeeRegistration(id, "DIAGNOSTIC", "Diagnostico", 4000.0) } returns AppResult.Success(expected)

        val result = updateEmployeeRegistrationUseCase(id, "DIAGNOSTIC", "Diagnostico", 4000.0)
        assertTrue(result is AppResult.Success)
        assertEquals(4000.0, (result as AppResult.Success).data.salary, 0.001)
    }

    @Test
    fun `deleteEmployeeRegistration calls repository`() = runTest {
        val id = UUID.randomUUID()
        coEvery { fleetRepository.deleteEmployeeRegistration(id) } returns AppResult.Success(Unit)

        val result = deleteEmployeeRegistrationUseCase(id)
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { fleetRepository.deleteEmployeeRegistration(id) }
    }

    @Test
    fun `getStaffCareerHistory returns career snapshots`() = runTest {
        val branchId = UUID.randomUUID()
        val employeeId = UUID.randomUUID()
        val expected = listOf(
            StaffCareerHistory(
                id = "hist-1",
                employeeId = employeeId,
                branchId = branchId,
                speciality = "GENERAL_MECHANIC",
                salary = 3000.0,
                status = "ACTIVE",
                changedAt = "2026-10-15T10:00:00Z"
            )
        )
        coEvery { fleetRepository.getStaffCareerHistory(branchId, employeeId) } returns AppResult.Success(expected)

        val result = getStaffCareerHistoryUseCase(branchId, employeeId)
        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
    }

    // === Customer Registrations ===

    @Test
    fun `getCustomerRegistrations with no filters returns validation error`() = runTest {
        val result = getCustomerRegistrationsUseCase(null, null, null)
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
    }

    @Test
    fun `updateCustomerRegistration with invalid status returns validation error`() = runTest {
        val result = updateCustomerRegistrationUseCase(UUID.randomUUID(), "SUSPENDED")
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
    }

    @Test
    fun `updateCustomerRegistration with ACTIVE calls repository`() = runTest {
        val id = UUID.randomUUID()
        val expected = CustomerRegistration(
            id = id,
            customerId = UUID.randomUUID(),
            branchId = UUID.randomUUID(),
            status = "ACTIVE"
        )
        coEvery { fleetRepository.updateCustomerRegistration(id, "ACTIVE") } returns AppResult.Success(expected)

        val result = updateCustomerRegistrationUseCase(id, "ACTIVE")
        assertTrue(result is AppResult.Success)
        assertEquals("ACTIVE", (result as AppResult.Success).data.status)
    }

    @Test
    fun `deleteCustomerRegistration calls repository`() = runTest {
        val id = UUID.randomUUID()
        coEvery { fleetRepository.deleteCustomerRegistration(id) } returns AppResult.Success(Unit)

        val result = deleteCustomerRegistrationUseCase(id)
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { fleetRepository.deleteCustomerRegistration(id) }
    }
}
