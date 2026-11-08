package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.SpecialtyRevision
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.WorkshopSpecialty
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateSpecialtyUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateWorkshopUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.DeleteSpecialtyUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetSpecialtiesUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetSpecialtyByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetSpecialtyRevisionHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetWorkshopByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetWorkshopsUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateSpecialtyUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateWorkshopUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WorkshopUseCasesTest {

    private val coreRepository: CoreRepository = mockk()
    private lateinit var getWorkshopsUseCase: GetWorkshopsUseCase
    private lateinit var getWorkshopByIdUseCase: GetWorkshopByIdUseCase
    private lateinit var createWorkshopUseCase: CreateWorkshopUseCase
    private lateinit var updateWorkshopUseCase: UpdateWorkshopUseCase
    private lateinit var getSpecialtiesUseCase: GetSpecialtiesUseCase
    private lateinit var getSpecialtyByIdUseCase: GetSpecialtyByIdUseCase
    private lateinit var createSpecialtyUseCase: CreateSpecialtyUseCase
    private lateinit var updateSpecialtyUseCase: UpdateSpecialtyUseCase
    private lateinit var deleteSpecialtyUseCase: DeleteSpecialtyUseCase
    private lateinit var getSpecialtyRevisionHistoryUseCase: GetSpecialtyRevisionHistoryUseCase

    @Before
    fun setUp() {
        getWorkshopsUseCase = GetWorkshopsUseCase(coreRepository)
        getWorkshopByIdUseCase = GetWorkshopByIdUseCase(coreRepository)
        createWorkshopUseCase = CreateWorkshopUseCase(coreRepository)
        updateWorkshopUseCase = UpdateWorkshopUseCase(coreRepository)
        getSpecialtiesUseCase = GetSpecialtiesUseCase(coreRepository)
        getSpecialtyByIdUseCase = GetSpecialtyByIdUseCase(coreRepository)
        createSpecialtyUseCase = CreateSpecialtyUseCase(coreRepository)
        updateSpecialtyUseCase = UpdateSpecialtyUseCase(coreRepository)
        deleteSpecialtyUseCase = DeleteSpecialtyUseCase(coreRepository)
        getSpecialtyRevisionHistoryUseCase = GetSpecialtyRevisionHistoryUseCase(coreRepository)
    }

    @Test
    fun `getWorkshops with blank ownerId returns validation error`() = runTest {
        val result = getWorkshopsUseCase("")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        coVerify(exactly = 0) { coreRepository.getWorkshops(any()) }
    }

    @Test
    fun `createWorkshop with invalid taxId length returns validation error`() = runTest {
        val result = createWorkshopUseCase(
            ownerId = "o1",
            businessName = "Mi Taller SAC",
            brandName = "Taller Central",
            taxId = "123456" // Less than 11 digits
        )
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("taxId", (error as AppError.Validation).details)
        coVerify(exactly = 0) { coreRepository.createWorkshop(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createWorkshop with valid parameters calls repository successfully`() = runTest {
        val expectedWorkshop = Workshop(
            id = "w1",
            ownerId = "o1",
            businessName = "TuxLogic Motors S.A.C.",
            brandName = "TuxLogic AutoCare",
            taxId = "20601234567",
            mileageIntervalConfig = 5000
        )

        coEvery {
            coreRepository.createWorkshop("o1", "TuxLogic Motors S.A.C.", "TuxLogic AutoCare", "20601234567", 5000)
        } returns AppResult.Success(expectedWorkshop)

        val result = createWorkshopUseCase(
            ownerId = "o1",
            businessName = "TuxLogic Motors S.A.C.",
            brandName = "TuxLogic AutoCare",
            taxId = "20601234567",
            mileageIntervalConfig = 5000
        )

        assertTrue(result is AppResult.Success)
        assertEquals(expectedWorkshop, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            coreRepository.createWorkshop("o1", "TuxLogic Motors S.A.C.", "TuxLogic AutoCare", "20601234567", 5000)
        }
    }

    @Test
    fun `updateWorkshop with valid parameters calls repository successfully`() = runTest {
        val expectedWorkshop = Workshop(
            id = "w1",
            ownerId = "o1",
            businessName = "TuxLogic Motors Updated S.A.C.",
            brandName = "TuxLogic Pro",
            taxId = "20601234567",
            mileageIntervalConfig = 7500
        )

        coEvery {
            coreRepository.updateWorkshop("w1", "TuxLogic Motors Updated S.A.C.", "TuxLogic Pro", "20601234567", 7500)
        } returns AppResult.Success(expectedWorkshop)

        val result = updateWorkshopUseCase("w1", "TuxLogic Motors Updated S.A.C.", "TuxLogic Pro", "20601234567", 7500)
        assertTrue(result is AppResult.Success)
        assertEquals(expectedWorkshop, (result as AppResult.Success).data)
    }

    @Test
    fun `createSpecialty with valid data calls repository`() = runTest {
        val expectedSpecialty = WorkshopSpecialty(
            id = "spec-1",
            workshopId = "w1",
            name = "Inyeccion Electronica",
            code = "INYECCION_ELECTRONICA",
            description = "Diagnostico y calibracion"
        )
        coEvery {
            coreRepository.createSpecialty("w1", "Inyeccion Electronica", "INYECCION_ELECTRONICA", "Diagnostico y calibracion")
        } returns AppResult.Success(expectedSpecialty)

        val result = createSpecialtyUseCase("w1", "Inyeccion Electronica", "INYECCION_ELECTRONICA", "Diagnostico y calibracion")
        assertTrue(result is AppResult.Success)
        assertEquals(expectedSpecialty, (result as AppResult.Success).data)
    }

    @Test
    fun `deleteSpecialty with valid ids calls repository`() = runTest {
        coEvery { coreRepository.deleteSpecialty("w1", "spec-1") } returns AppResult.Success(Unit)

        val result = deleteSpecialtyUseCase("w1", "spec-1")
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { coreRepository.deleteSpecialty("w1", "spec-1") }
    }

    @Test
    fun `getSpecialtyHistory with blank workshopId returns validation error`() = runTest {
        val result = getSpecialtyRevisionHistoryUseCase("", "spec-1")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("workshopId", (error as AppError.Validation).details)
    }

    @Test
    fun `getSpecialtyHistory with blank specialtyId returns validation error`() = runTest {
        val result = getSpecialtyRevisionHistoryUseCase("w1", "")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("specialtyId", (error as AppError.Validation).details)
    }

    @Test
    fun `getSpecialtyHistory with valid ids calls repository and returns revisions`() = runTest {
        val revisions = listOf(
            SpecialtyRevision(
                id = "rev-1",
                specialtyId = "spec-1",
                fieldName = "name",
                oldValue = "Old Name",
                newValue = "New Name",
                changedBy = "usr-1",
                changedAt = "2026-11-01T10:00:00Z"
            )
        )
        coEvery { coreRepository.getSpecialtyHistory("w1", "spec-1") } returns AppResult.Success(revisions)

        val result = getSpecialtyRevisionHistoryUseCase("w1", "spec-1")
        assertTrue(result is AppResult.Success)
        assertEquals(revisions, (result as AppResult.Success).data)
        coVerify(exactly = 1) { coreRepository.getSpecialtyHistory("w1", "spec-1") }
    }
}
