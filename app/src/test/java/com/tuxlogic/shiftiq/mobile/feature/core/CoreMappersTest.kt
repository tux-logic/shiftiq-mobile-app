package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.BranchResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.BranchSubscriptionResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.CustomerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.DocumentProfileLookupDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.EmployeeResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.OwnerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UnifiedEmployeeSummaryDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.UnifiedProfileResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.WorkshopResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.data.remote.dto.WorkshopSpecialtyResourceDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreMappersTest {

    @Test
    fun `WorkshopResourceDto maps correctly to domain Workshop`() {
        val dto = WorkshopResourceDto(
            id = "w-1",
            ownerId = "o-1",
            businessName = "Castro Motors SAC",
            brandName = "Castro Motors",
            taxId = "20549876123",
            mileageIntervalConfig = 6000
        )
        val domain = dto.toDomain()
        assertEquals("w-1", domain.id)
        assertEquals("o-1", domain.ownerId)
        assertEquals("Castro Motors SAC", domain.businessName)
        assertEquals("Castro Motors", domain.brandName)
        assertEquals("20549876123", domain.taxId)
        assertEquals(6000, domain.mileageIntervalConfig)
    }

    @Test
    fun `WorkshopSpecialtyResourceDto maps correctly to domain WorkshopSpecialty`() {
        val dto = WorkshopSpecialtyResourceDto(
            id = "s-1",
            workshopId = "w-1",
            name = "Mecanica General",
            code = "GENERAL_MECHANIC",
            description = "Motores y frenos",
            active = true
        )
        val domain = dto.toDomain()
        assertEquals("s-1", domain.id)
        assertEquals("w-1", domain.workshopId)
        assertEquals("Mecanica General", domain.name)
        assertEquals("GENERAL_MECHANIC", domain.code)
        assertTrue(domain.active)
    }

    @Test
    fun `BranchResourceDto maps correctly to domain Branch`() {
        val dto = BranchResourceDto(
            id = "b-1",
            workshopId = "w-1",
            code = "BR-SURCO-01",
            name = "Sede Surco",
            address = "Av Primavera 123",
            phone = "+51987654321"
        )
        val domain = dto.toDomain()
        assertEquals(BranchId("b-1"), domain.id)
        assertEquals("w-1", domain.workshopId)
        assertEquals("BR-SURCO-01", domain.code)
        assertEquals("Sede Surco", domain.name)
    }

    @Test
    fun `BranchSubscriptionResourceDto maps correctly to domain BranchSubscription`() {
        val dto = BranchSubscriptionResourceDto(
            id = "sub-1",
            branchId = "b-1",
            planId = "plan-pro",
            billingCycle = "MONTHLY",
            status = "ACTIVE",
            startDate = "2026-10-01T00:00:00Z",
            endDate = "2026-11-01T00:00:00Z"
        )
        val domain = dto.toDomain()
        assertEquals("sub-1", domain.id)
        assertEquals(BranchId("b-1"), domain.branchId)
        assertEquals("ACTIVE", domain.status)
    }

    @Test
    fun `OwnerResourceDto maps correctly to domain OwnerProfile`() {
        val dto = OwnerResourceDto(
            id = "o-1",
            userId = "u-1",
            firstName = "Raul",
            lastName = "Castro",
            documentType = "DNI",
            documentNumber = "45678901",
            phone = "+51987112233"
        )
        val domain = dto.toDomain()
        assertEquals("o-1", domain.id)
        assertEquals(UserId("u-1"), domain.userId)
        assertEquals("Raul Castro", domain.fullName)
    }

    @Test
    fun `CustomerResourceDto maps correctly to domain CustomerProfile`() {
        val naturalDto = CustomerResourceDto(
            id = "c-1",
            userId = "u-2",
            isCorporate = false,
            firstName = "Maria",
            lastName = "Fernandez",
            businessName = null,
            documentType = "DNI",
            documentNumber = "48765432",
            phone = "+51922334455"
        )
        val naturalDomain = naturalDto.toDomain()
        assertEquals("c-1", naturalDomain.id)
        assertFalse(naturalDomain.isCorporate)
        assertEquals("Maria Fernandez", naturalDomain.displayName)

        val corporateDto = CustomerResourceDto(
            id = "c-2",
            userId = "u-3",
            isCorporate = true,
            firstName = null,
            lastName = null,
            businessName = "Logistica SAC",
            documentType = "RUC",
            documentNumber = "20601234567",
            phone = "+51988776655"
        )
        val corporateDomain = corporateDto.toDomain()
        assertTrue(corporateDomain.isCorporate)
        assertEquals("Logistica SAC", corporateDomain.displayName)
    }

    @Test
    fun `EmployeeResourceDto maps correctly to domain EmployeeProfile`() {
        val dto = EmployeeResourceDto(
            id = "e-1",
            userId = "u-4",
            firstName = "Luis",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "71234567",
            phone = "+51912345678"
        )
        val domain = dto.toDomain()
        assertEquals("e-1", domain.id)
        assertEquals(UserId("u-4"), domain.userId)
        assertEquals("Luis Mendoza", domain.fullName)
    }

    @Test
    fun `UnifiedProfileResourceDto maps correctly to domain UnifiedProfile`() {
        val dto = UnifiedProfileResourceDto(
            userId = "u-1",
            roles = listOf("ROLE_EMPLOYEE"),
            employee = UnifiedEmployeeSummaryDto(
                employeeId = "emp-1",
                workshopId = "w-1",
                firstName = "Luis",
                lastName = "Mendoza",
                documentType = "DNI",
                documentNumber = "71234567",
                phone = "+51912345678"
            )
        )
        val domain = dto.toDomain()
        assertEquals(UserId("u-1"), domain.userId)
        assertEquals(listOf("ROLE_EMPLOYEE"), domain.roles)
        assertNotNull(domain.employee)
        assertEquals("emp-1", domain.employee?.employeeId)
        assertEquals("Luis Mendoza", domain.employee?.fullName)
    }

    @Test
    fun `DocumentProfileLookupDto maps correctly to domain DocumentProfileLookup`() {
        val dto = DocumentProfileLookupDto(
            profileType = "EMPLOYEE",
            userId = "u-1",
            firstName = "Luis",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "71234567",
            phone = "+51912345678",
            employeeId = "emp-1",
            roles = listOf("ROLE_EMPLOYEE")
        )
        val domain = dto.toDomain()
        assertEquals("EMPLOYEE", domain.profileType)
        assertEquals(UserId("u-1"), domain.userId)
        assertEquals("Luis Mendoza", domain.fullName)
    }
}
