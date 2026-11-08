package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.api.CoreApiService
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.BranchResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.BranchSubscriptionResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.CustomerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.DocumentProfileLookupDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.EmployeeResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.OwnerResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.SpecialtyRevisionResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.UnifiedProfileResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.WorkshopResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto.WorkshopSpecialtyResourceDto
import com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.repository.CoreRepositoryImpl
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

class CoreRepositoryTest {

    private val apiService: CoreApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private lateinit var repository: CoreRepositoryImpl

    @Before
    fun setUp() {
        repository = CoreRepositoryImpl(
            coreApiService = apiService,
            sessionDataStore = sessionDataStore,
            dispatchers = dispatchers
        )
    }

    // === Workshops ===

    @Test
    fun `getWorkshops returns success when API responds 200`() = runTest(testDispatcher) {
        val dtos = listOf(
            WorkshopResourceDto(
                id = "w-1",
                ownerId = "o-1",
                businessName = "Castro Motors SAC",
                brandName = "Castro Motors",
                taxId = "20549876123",
                mileageIntervalConfig = 5000
            )
        )
        coEvery { apiService.getWorkshops("o-1") } returns Response.success(dtos)

        val result = repository.getWorkshops("o-1")
        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
        assertEquals("w-1", result.data[0].id)
    }

    @Test
    fun `updateWorkshop returns success when API responds 200`() = runTest(testDispatcher) {
        val dto = WorkshopResourceDto(
            id = "w-1",
            ownerId = "o-1",
            businessName = "Castro Motors Updated",
            brandName = "Castro Motors Pro",
            taxId = "20549876123",
            mileageIntervalConfig = 7500
        )
        coEvery { apiService.updateWorkshop("w-1", any()) } returns Response.success(dto)

        val result = repository.updateWorkshop("w-1", "Castro Motors Updated", "Castro Motors Pro", "20549876123", 7500)
        assertTrue(result is AppResult.Success)
        assertEquals(7500, (result as AppResult.Success).data.mileageIntervalConfig)
    }

    // === Specialties ===

    @Test
    fun `createSpecialty calls API and returns domain specialty`() = runTest(testDispatcher) {
        val dto = WorkshopSpecialtyResourceDto(
            id = "s-1",
            workshopId = "w-1",
            name = "Inyeccion",
            code = "INYECCION",
            description = "Calibracion"
        )
        coEvery { apiService.createSpecialty("w-1", any()) } returns Response.success(dto)

        val result = repository.createSpecialty("w-1", "Inyeccion", "INYECCION", "Calibracion")
        assertTrue(result is AppResult.Success)
        assertEquals("s-1", (result as AppResult.Success).data.id)
    }

    @Test
    fun `deleteSpecialty returns success on 204`() = runTest(testDispatcher) {
        coEvery { apiService.deleteSpecialty("w-1", "s-1") } returns Response.success(Unit)

        val result = repository.deleteSpecialty("w-1", "s-1")
        assertTrue(result is AppResult.Success)
    }

    // === Branches & Subscriptions ===

    @Test
    fun `assignSubscription returns domain BranchSubscription`() = runTest(testDispatcher) {
        val dto = BranchSubscriptionResourceDto(
            id = "sub-1",
            branchId = "b-1",
            planId = "plan-1",
            billingCycle = "MONTHLY",
            status = "ACTIVE",
            startDate = "2026-10-01T00:00:00Z",
            endDate = "2026-11-01T00:00:00Z"
        )
        coEvery { apiService.assignBranchSubscription("b-1", any()) } returns Response.success(dto)

        val result = repository.assignSubscription("b-1", "plan-1", "MONTHLY", "4532015698451234", "Carlos", "12/28", "123")
        assertTrue(result is AppResult.Success)
        assertEquals("sub-1", (result as AppResult.Success).data.id)
        assertEquals("ACTIVE", result.data.status)
    }

    @Test
    fun `cancelSubscription returns success on 204`() = runTest(testDispatcher) {
        coEvery { apiService.cancelBranchSubscription("b-1") } returns Response.success(Unit)

        val result = repository.cancelSubscription("b-1")
        assertTrue(result is AppResult.Success)
    }

    @Test
    fun `setActiveBranch stores branch in SessionDataStore`() = runTest(testDispatcher) {
        val branchId = BranchId("b-1")
        val result = repository.setActiveBranch(branchId)
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { sessionDataStore.setActiveBranchId(branchId) }
    }

    // === Customer Profiles ===

    @Test
    fun `createCustomerProfile returns success with domain CustomerProfile`() = runTest(testDispatcher) {
        val dto = CustomerResourceDto(
            id = "c-1",
            userId = "u-1",
            isCorporate = false,
            firstName = "Maria",
            lastName = "Fernandez",
            businessName = null,
            documentType = "DNI",
            documentNumber = "48765432",
            phone = "+51922334455"
        )
        coEvery { apiService.createCustomer(any()) } returns Response.success(dto)

        val result = repository.createCustomerProfile("u-1", "Maria", "Fernandez", null, "DNI", "48765432", "+51922334455")
        assertTrue(result is AppResult.Success)
        assertEquals("c-1", (result as AppResult.Success).data.id)
    }

    @Test
    fun `deleteCustomerProfile returns failure when API returns 404`() = runTest(testDispatcher) {
        coEvery { apiService.deleteCustomer("c-1") } returns Response.error(404, "".toResponseBody())

        val result = repository.deleteCustomerProfile("c-1")
        assertTrue(result is AppResult.Failure)
    }

    // === Employee Profiles ===

    @Test
    fun `getEmployeeByDocumentNumber returns domain EmployeeProfile`() = runTest(testDispatcher) {
        val dto = EmployeeResourceDto(
            id = "e-1",
            userId = "u-2",
            firstName = "Luis",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "71234567",
            phone = "+51912345678"
        )
        coEvery { apiService.getEmployeeByDocumentNumber("71234567") } returns Response.success(dto)

        val result = repository.getEmployeeByDocumentNumber("71234567")
        assertTrue(result is AppResult.Success)
        assertEquals("e-1", (result as AppResult.Success).data.id)
        assertEquals("Luis Mendoza", result.data.fullName)
    }

    // === Unified Profiles ===

    @Test
    fun `getProfileMe returns domain UnifiedProfile`() = runTest(testDispatcher) {
        val dto = UnifiedProfileResourceDto(
            userId = "u-1",
            roles = listOf("ROLE_EMPLOYEE")
        )
        coEvery { apiService.getProfileMe() } returns Response.success(dto)

        val result = repository.getProfileMe()
        assertTrue(result is AppResult.Success)
        assertEquals(UserId("u-1"), (result as AppResult.Success).data.userId)
        assertEquals(listOf("ROLE_EMPLOYEE"), result.data.roles)
    }

    @Test
    fun `lookupProfileByDocumentNumber returns domain DocumentProfileLookup`() = runTest(testDispatcher) {
        val dto = DocumentProfileLookupDto(
            profileType = "EMPLOYEE",
            userId = "u-1",
            firstName = "Luis",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "71234567",
            phone = "+51912345678",
            employeeId = "e-1",
            roles = listOf("ROLE_EMPLOYEE")
        )
        coEvery { apiService.lookupProfileByDocumentNumber("71234567") } returns Response.success(dto)

        val result = repository.lookupProfileByDocumentNumber("71234567")
        assertTrue(result is AppResult.Success)
        assertEquals("EMPLOYEE", (result as AppResult.Success).data.profileType)
    }

    @Test
    fun `getSpecialtyHistory returns list of domain SpecialtyRevision`() = runTest(testDispatcher) {
        val dtos = listOf(
            SpecialtyRevisionResourceDto(
                id = "rev-1",
                specialtyId = "spec-1",
                fieldName = "name",
                oldValue = "Old",
                newValue = "New",
                changedBy = "usr-1",
                changedAt = "2026-11-01T10:00:00Z"
            )
        )
        coEvery { apiService.getSpecialtyHistory("w-1", "spec-1") } returns Response.success(dtos)

        val result = repository.getSpecialtyHistory("w-1", "spec-1")
        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(1, data.size)
        assertEquals("rev-1", data[0].id)
        assertEquals("name", data[0].fieldName)
        assertEquals("Old", data[0].oldValue)
        assertEquals("New", data[0].newValue)
    }
}
