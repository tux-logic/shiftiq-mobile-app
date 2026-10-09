package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.CreateWorkshopUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.GetWorkshopsUseCase
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
    private lateinit var createWorkshopUseCase: CreateWorkshopUseCase

    @Before
    fun setUp() {
        getWorkshopsUseCase = GetWorkshopsUseCase(coreRepository)
        createWorkshopUseCase = CreateWorkshopUseCase(coreRepository)
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
            taxId = "123456" // Menor a 11 digitos
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
}
