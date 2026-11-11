package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.CustomerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.DocumentProfileLookup
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.EmployeeProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateCustomerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateEmployeeProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.DeleteCustomerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.DeleteEmployeeProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.DeleteOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetEmployeeByDocumentNumberUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetEmployeeByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetEmployeeByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetOwnerByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetRolesByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetUnifiedProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.LookupProfileByDocumentUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateCustomerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateEmployeeProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateOwnerProfileUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileUseCasesTest {

    private val repository: CoreRepository = mockk()

    private lateinit var getUnifiedProfileUseCase: GetUnifiedProfileUseCase
    private lateinit var getRolesByUserIdUseCase: GetRolesByUserIdUseCase
    private lateinit var lookupProfileByDocumentUseCase: LookupProfileByDocumentUseCase

    private lateinit var getOwnerProfileUseCase: GetOwnerProfileUseCase
    private lateinit var getOwnerByIdUseCase: GetOwnerByIdUseCase
    private lateinit var createOwnerProfileUseCase: CreateOwnerProfileUseCase
    private lateinit var updateOwnerProfileUseCase: UpdateOwnerProfileUseCase
    private lateinit var deleteOwnerProfileUseCase: DeleteOwnerProfileUseCase

    private lateinit var createCustomerProfileUseCase: CreateCustomerProfileUseCase
    private lateinit var getCustomerByIdUseCase: GetCustomerByIdUseCase
    private lateinit var getCustomerByUserIdUseCase: GetCustomerByUserIdUseCase
    private lateinit var updateCustomerProfileUseCase: UpdateCustomerProfileUseCase
    private lateinit var deleteCustomerProfileUseCase: DeleteCustomerProfileUseCase

    private lateinit var createEmployeeProfileUseCase: CreateEmployeeProfileUseCase
    private lateinit var getEmployeeByIdUseCase: GetEmployeeByIdUseCase
    private lateinit var getEmployeeByUserIdUseCase: GetEmployeeByUserIdUseCase
    private lateinit var getEmployeeByDocumentNumberUseCase: GetEmployeeByDocumentNumberUseCase
    private lateinit var updateEmployeeProfileUseCase: UpdateEmployeeProfileUseCase
    private lateinit var deleteEmployeeProfileUseCase: DeleteEmployeeProfileUseCase

    @Before
    fun setUp() {
        getUnifiedProfileUseCase = GetUnifiedProfileUseCase(repository)
        getRolesByUserIdUseCase = GetRolesByUserIdUseCase(repository)
        lookupProfileByDocumentUseCase = LookupProfileByDocumentUseCase(repository)

        getOwnerProfileUseCase = GetOwnerProfileUseCase(repository)
        getOwnerByIdUseCase = GetOwnerByIdUseCase(repository)
        createOwnerProfileUseCase = CreateOwnerProfileUseCase(repository)
        updateOwnerProfileUseCase = UpdateOwnerProfileUseCase(repository)
        deleteOwnerProfileUseCase = DeleteOwnerProfileUseCase(repository)

        createCustomerProfileUseCase = CreateCustomerProfileUseCase(repository)
        getCustomerByIdUseCase = GetCustomerByIdUseCase(repository)
        getCustomerByUserIdUseCase = GetCustomerByUserIdUseCase(repository)
        updateCustomerProfileUseCase = UpdateCustomerProfileUseCase(repository)
        deleteCustomerProfileUseCase = DeleteCustomerProfileUseCase(repository)

        createEmployeeProfileUseCase = CreateEmployeeProfileUseCase(repository)
        getEmployeeByIdUseCase = GetEmployeeByIdUseCase(repository)
        getEmployeeByUserIdUseCase = GetEmployeeByUserIdUseCase(repository)
        getEmployeeByDocumentNumberUseCase = GetEmployeeByDocumentNumberUseCase(repository)
        updateEmployeeProfileUseCase = UpdateEmployeeProfileUseCase(repository)
        deleteEmployeeProfileUseCase = DeleteEmployeeProfileUseCase(repository)
    }

    // === Unified Profiles & Lookups ===

    @Test
    fun `getUnifiedProfile returns success from repository`() = runTest {
        val expected = UnifiedProfile(userId = UserId("u-1"), roles = listOf("ROLE_OWNER"))
        coEvery { repository.getProfileMe() } returns AppResult.Success(expected)

        val result = getUnifiedProfileUseCase()
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
    }

    @Test
    fun `getRolesByUserId with blank userId returns validation error`() = runTest {
        val result = getRolesByUserIdUseCase("   ")
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
    }

    @Test
    fun `lookupProfileByDocument with valid document returns lookup result`() = runTest {
        val expected = DocumentProfileLookup(
            profileType = "EMPLOYEE",
            userId = UserId("u-1"),
            firstName = "Luis",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "71234567",
            phone = "+51912345678"
        )
        coEvery { repository.lookupProfileByDocumentNumber("71234567") } returns AppResult.Success(expected)

        val result = lookupProfileByDocumentUseCase("71234567")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
    }

    // === Owner Profile ===

    @Test
    fun `createOwnerProfile with valid data calls repository`() = runTest {
        val expected = OwnerProfile(
            id = "o-1",
            userId = UserId("u-1"),
            firstName = "Carlos",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "45678901",
            phone = "+51987112233"
        )
        coEvery {
            repository.createOwnerProfile("u-1", "Carlos", "Mendoza", "DNI", "45678901", "+51987112233")
        } returns AppResult.Success(expected)

        val result = createOwnerProfileUseCase("u-1", "Carlos", "Mendoza", "DNI", "45678901", "+51987112233")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
    }

    @Test
    fun `deleteOwnerProfile with valid ownerId calls repository`() = runTest {
        coEvery { repository.deleteOwnerProfile("o-1") } returns AppResult.Success(Unit)

        val result = deleteOwnerProfileUseCase("o-1")
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { repository.deleteOwnerProfile("o-1") }
    }

    // === Customer Profile ===

    @Test
    fun `createCustomerProfile corporate without businessName returns validation error`() = runTest {
        val result = createCustomerProfileUseCase(
            userId = "u-2",
            firstName = null,
            lastName = null,
            businessName = null,
            documentType = "RUC",
            documentNumber = "20601234567",
            phone = "+51987654321"
        )
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
    }

    @Test
    fun `createCustomerProfile with valid corporate data calls repository`() = runTest {
        val expected = CustomerProfile(
            id = "c-1",
            userId = UserId("u-2"),
            isCorporate = true,
            firstName = null,
            lastName = null,
            businessName = "Transportes SAC",
            documentType = "RUC",
            documentNumber = "20601234567",
            phone = "+51987654321"
        )
        coEvery {
            repository.createCustomerProfile("u-2", null, null, "Transportes SAC", "RUC", "20601234567", "+51987654321")
        } returns AppResult.Success(expected)

        val result = createCustomerProfileUseCase("u-2", null, null, "Transportes SAC", "RUC", "20601234567", "+51987654321")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
    }

    // === Employee Profile ===

    @Test
    fun `createEmployeeProfile with valid data calls repository`() = runTest {
        val expected = EmployeeProfile(
            id = "e-1",
            userId = UserId("u-3"),
            firstName = "Luis",
            lastName = "Mendoza",
            documentType = "DNI",
            documentNumber = "71234567",
            phone = "+51912345678"
        )
        coEvery {
            repository.createEmployeeProfile("u-3", "Luis", "Mendoza", "DNI", "71234567", "+51912345678")
        } returns AppResult.Success(expected)

        val result = createEmployeeProfileUseCase("u-3", "Luis", "Mendoza", "DNI", "71234567", "+51912345678")
        assertTrue(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
    }

    @Test
    fun `getEmployeeByDocumentNumber with blank document returns validation error`() = runTest {
        val result = getEmployeeByDocumentNumberUseCase("   ")
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
    }
}
