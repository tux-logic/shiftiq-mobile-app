package com.tuxlogic.shiftiq.mobile.feature.fleet

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.api.FleetApiService
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.AppointmentDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.CustomerRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.EmployeeRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.StaffCareerHistoryDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.repository.FleetRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class FleetRepositoryTest {

    private val apiService: FleetApiService = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private lateinit var repository: FleetRepositoryImpl

    @Before
    fun setUp() {
        repository = FleetRepositoryImpl(
            fleetApiService = apiService,
            dispatchers = dispatchers
        )
    }

    // === Appointments ===

    @Test
    fun `getAppointments returns domain list on 200`() = runTest(testDispatcher) {
        val branchId = UUID.randomUUID()
        val dtos = listOf(
            AppointmentDto(
                id = UUID.randomUUID().toString(),
                branchId = branchId.toString(),
                customerId = UUID.randomUUID().toString(),
                vehicleId = UUID.randomUUID().toString(),
                scheduledStart = "2026-10-15T09:00:00",
                status = "PENDING"
            )
        )
        coEvery { apiService.getAppointments(branchId = branchId.toString(), any(), any(), any()) } returns Response.success(dtos)

        val result = repository.getAppointments(branchId = branchId)
        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
    }

    @Test
    fun `deleteAppointment returns success on 204`() = runTest(testDispatcher) {
        val id = UUID.randomUUID()
        coEvery { apiService.deleteAppointment(id.toString()) } returns Response.success(Unit)

        val result = repository.deleteAppointment(id)
        assertTrue(result is AppResult.Success)
    }

    // === Employee Registrations ===

    @Test
    fun `updateEmployeeRegistration returns domain updated registration`() = runTest(testDispatcher) {
        val id = UUID.randomUUID()
        val employeeId = UUID.randomUUID()
        val branchId = UUID.randomUUID()

        val dto = EmployeeRegistrationDto(
            id = id.toString(),
            employeeId = employeeId.toString(),
            branchId = branchId.toString(),
            speciality = "DIAGNOSTIC",
            specialityName = "Diagnostico Computarizado",
            salary = 3800.0,
            role = "ROLE_EMPLOYEE",
            status = "ACTIVE"
        )
        coEvery { apiService.updateEmployeeRegistration(id.toString(), any()) } returns Response.success(dto)

        val result = repository.updateEmployeeRegistration(id, "DIAGNOSTIC", "Diagnostico Computarizado", 3800.0)
        assertTrue(result is AppResult.Success)
        assertEquals(3800.0, (result as AppResult.Success).data.salary, 0.001)
    }

    @Test
    fun `deleteEmployeeRegistration returns success on 204`() = runTest(testDispatcher) {
        val id = UUID.randomUUID()
        coEvery { apiService.deleteEmployeeRegistration(id.toString()) } returns Response.success(Unit)

        val result = repository.deleteEmployeeRegistration(id)
        assertTrue(result is AppResult.Success)
    }

    @Test
    fun `getStaffCareerHistory returns domain history list`() = runTest(testDispatcher) {
        val branchId = UUID.randomUUID()
        val employeeId = UUID.randomUUID()
        val dtos = listOf(
            StaffCareerHistoryDto(
                id = "hist-1",
                employeeId = employeeId.toString(),
                branchId = branchId.toString(),
                speciality = "GENERAL_MECHANIC",
                salary = 3000.0,
                status = "ACTIVE",
                changedAt = "2026-10-15T10:00:00Z"
            )
        )
        coEvery { apiService.getStaffCareerHistory(branchId.toString(), employeeId.toString()) } returns Response.success(dtos)

        val result = repository.getStaffCareerHistory(branchId, employeeId)
        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        assertEquals("hist-1", result.data[0].id)
    }

    // === Customer Registrations ===

    @Test
    fun `getCustomerRegistrationById returns customer registration`() = runTest(testDispatcher) {
        val id = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val branchId = UUID.randomUUID()

        val dto = CustomerRegistrationDto(
            id = id.toString(),
            customerId = customerId.toString(),
            branchId = branchId.toString(),
            status = "ACTIVE"
        )
        coEvery { apiService.getCustomerRegistrationById(id.toString()) } returns Response.success(dto)

        val result = repository.getCustomerRegistrationById(id)
        assertTrue(result is AppResult.Success)
        assertEquals(id, (result as AppResult.Success).data.id)
    }

    @Test
    fun `updateCustomerRegistration returns updated customer registration`() = runTest(testDispatcher) {
        val id = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val branchId = UUID.randomUUID()

        val dto = CustomerRegistrationDto(
            id = id.toString(),
            customerId = customerId.toString(),
            branchId = branchId.toString(),
            status = "INACTIVE"
        )
        coEvery { apiService.updateCustomerRegistration(id.toString(), any()) } returns Response.success(dto)

        val result = repository.updateCustomerRegistration(id, "INACTIVE")
        assertTrue(result is AppResult.Success)
        assertEquals("INACTIVE", (result as AppResult.Success).data.status)
    }

    @Test
    fun `deleteCustomerRegistration returns success on 204`() = runTest(testDispatcher) {
        val id = UUID.randomUUID()
        coEvery { apiService.deleteCustomerRegistration(id.toString()) } returns Response.success(Unit)

        val result = repository.deleteCustomerRegistration(id)
        assertTrue(result is AppResult.Success)
    }
}
