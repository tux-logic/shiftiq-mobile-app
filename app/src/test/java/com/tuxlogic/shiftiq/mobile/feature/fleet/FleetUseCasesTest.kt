package com.tuxlogic.shiftiq.mobile.feature.fleet

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.Appointment
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.repository.FleetRepository
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.ApproveEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.CreateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.GetAppointmentsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.GetEmployeeRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.RejectEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.UpdateAppointmentUseCase
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
    private lateinit var approveEmployeeRegistrationUseCase: ApproveEmployeeRegistrationUseCase
    private lateinit var rejectEmployeeRegistrationUseCase: RejectEmployeeRegistrationUseCase

    @Before
    fun setUp() {
        getAppointmentsUseCase = GetAppointmentsUseCase(fleetRepository)
        createAppointmentUseCase = CreateAppointmentUseCase(fleetRepository)
        updateAppointmentUseCase = UpdateAppointmentUseCase(fleetRepository)
        getEmployeeRegistrationsUseCase = GetEmployeeRegistrationsUseCase(fleetRepository)
        approveEmployeeRegistrationUseCase = ApproveEmployeeRegistrationUseCase(fleetRepository)
        rejectEmployeeRegistrationUseCase = RejectEmployeeRegistrationUseCase(fleetRepository)
    }

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

    @Test
    fun `getEmployeeRegistrations returns staff registrations filtered by branch`() = runTest {
        val branchId = UUID.randomUUID()
        val mockRegistrations = listOf(
            EmployeeRegistration(
                id = UUID.randomUUID(),
                employeeId = UUID.randomUUID(),
                branchId = branchId,
                speciality = "Mecánica",
                specialityName = "Mecánica",
                salary = 2500.0,
                role = "ROLE_TECHNICIAN",
                status = "ACTIVE"
            )
        )

        coEvery { fleetRepository.getEmployeeRegistrations(branchId = branchId) } returns AppResult.Success(mockRegistrations)

        val result = getEmployeeRegistrationsUseCase(branchId = branchId)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        assertTrue(result.data.first().isActive)
    }

    @Test
    fun `approveEmployeeRegistration calls approve on repository and returns active registration`() = runTest {
        val registrationId = UUID.randomUUID()
        val approved = EmployeeRegistration(
            id = registrationId,
            employeeId = UUID.randomUUID(),
            branchId = UUID.randomUUID(),
            speciality = "Frenos",
            specialityName = "Frenos",
            salary = 3000.0,
            role = "ROLE_TECHNICIAN",
            status = "ACTIVE"
        )

        coEvery { fleetRepository.approveEmployeeRegistration(registrationId) } returns AppResult.Success(approved)

        val result = approveEmployeeRegistrationUseCase(registrationId)

        assertTrue(result is AppResult.Success)
        assertEquals("ACTIVE", (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { fleetRepository.approveEmployeeRegistration(registrationId) }
    }

    @Test
    fun `rejectEmployeeRegistration calls reject on repository with reason`() = runTest {
        val registrationId = UUID.randomUUID()
        val rejected = EmployeeRegistration(
            id = registrationId,
            employeeId = UUID.randomUUID(),
            branchId = UUID.randomUUID(),
            speciality = "Frenos",
            specialityName = "Frenos",
            salary = 3000.0,
            role = "ROLE_TECHNICIAN",
            status = "REJECTED"
        )

        coEvery { fleetRepository.rejectEmployeeRegistration(registrationId, "No vacantes") } returns AppResult.Success(rejected)

        val result = rejectEmployeeRegistrationUseCase(registrationId, "No vacantes")

        assertTrue(result is AppResult.Success)
        assertEquals("REJECTED", (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { fleetRepository.rejectEmployeeRegistration(registrationId, "No vacantes") }
    }
}
