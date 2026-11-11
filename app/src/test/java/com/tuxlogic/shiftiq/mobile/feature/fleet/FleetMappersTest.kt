package com.tuxlogic.shiftiq.mobile.feature.fleet

import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.AppointmentDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.CustomerRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.EmployeeRegistrationDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.infrastructure.remote.dto.StaffCareerHistoryDto
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.AppointmentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FleetMappersTest {

    @Test
    fun `AppointmentDto maps correctly to domain Appointment`() {
        val id = UUID.randomUUID()
        val branchId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val vehicleId = UUID.randomUUID()

        val dto = AppointmentDto(
            id = id.toString(),
            branchId = branchId.toString(),
            customerId = customerId.toString(),
            vehicleId = vehicleId.toString(),
            scheduledStart = "2026-10-15T09:00:00",
            scheduledEnd = "2026-10-15T10:00:00",
            status = "PENDING",
            notes = "Revision frenos"
        )

        val domain = dto.toDomain()
        assertEquals(id, domain.id)
        assertEquals(branchId, domain.branchId)
        assertEquals(customerId, domain.customerId)
        assertEquals(vehicleId, domain.vehicleId)
        assertEquals(AppointmentStatus.PENDING, domain.status)
        assertEquals("Revision frenos", domain.notes)
    }

    @Test
    fun `EmployeeRegistrationDto maps correctly to domain EmployeeRegistration`() {
        val id = UUID.randomUUID()
        val employeeId = UUID.randomUUID()
        val branchId = UUID.randomUUID()

        val dto = EmployeeRegistrationDto(
            id = id.toString(),
            employeeId = employeeId.toString(),
            branchId = branchId.toString(),
            speciality = "GENERAL_MECHANIC",
            specialityName = "Mecanica General",
            salary = 3500.0,
            role = "ROLE_EMPLOYEE",
            status = "ACTIVE"
        )

        val domain = dto.toDomain()
        assertEquals(id, domain.id)
        assertEquals(employeeId, domain.employeeId)
        assertEquals(branchId, domain.branchId)
        assertEquals("GENERAL_MECHANIC", domain.speciality)
        assertEquals(3500.0, domain.salary, 0.001)
        assertTrue(domain.isActive)
    }

    @Test
    fun `StaffCareerHistoryDto maps correctly to domain StaffCareerHistory`() {
        val employeeId = UUID.randomUUID()
        val branchId = UUID.randomUUID()

        val dto = StaffCareerHistoryDto(
            id = "hist-1",
            employeeId = employeeId.toString(),
            branchId = branchId.toString(),
            speciality = "GENERAL_MECHANIC",
            specialityName = "Mecanica General",
            salary = 3500.0,
            role = "ROLE_EMPLOYEE",
            status = "ACTIVE",
            changedAt = "2026-10-15T10:30:00Z",
            changeReason = "Promocion a senior"
        )

        val domain = dto.toDomain()
        assertEquals("hist-1", domain.id)
        assertEquals(employeeId, domain.employeeId)
        assertEquals(branchId, domain.branchId)
        assertEquals("Promocion a senior", domain.changeReason)
    }

    @Test
    fun `CustomerRegistrationDto maps correctly to domain CustomerRegistration`() {
        val id = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val branchId = UUID.randomUUID()

        val dto = CustomerRegistrationDto(
            id = id.toString(),
            customerId = customerId.toString(),
            branchId = branchId.toString(),
            status = "ACTIVE"
        )

        val domain = dto.toDomain()
        assertEquals(id, domain.id)
        assertEquals(customerId, domain.customerId)
        assertEquals(branchId, domain.branchId)
        assertTrue(domain.isActive)
    }
}
