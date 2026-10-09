package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.CreateBranchUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.GetBranchesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BranchUseCasesTest {

    private val coreRepository: CoreRepository = mockk()
    private lateinit var getBranchesUseCase: GetBranchesUseCase
    private lateinit var createBranchUseCase: CreateBranchUseCase

    @Before
    fun setUp() {
        getBranchesUseCase = GetBranchesUseCase(coreRepository)
        createBranchUseCase = CreateBranchUseCase(coreRepository)
    }

    @Test
    fun `getBranches with blank workshopId returns validation error`() = runTest {
        val result = getBranchesUseCase("")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        coVerify(exactly = 0) { coreRepository.getBranches(any()) }
    }

    @Test
    fun `createBranch with blank name returns validation error`() = runTest {
        val result = createBranchUseCase(
            workshopId = "w1",
            code = "SEDE-01",
            name = "",
            address = "Av. Javier Prado 123",
            phone = "+51987654321"
        )
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("name", (error as AppError.Validation).details)
        coVerify(exactly = 0) { coreRepository.createBranch(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createBranch with valid data calls repository and returns created branch`() = runTest {
        val expectedBranch = Branch(
            id = BranchId("b1"),
            workshopId = "w1",
            code = "SEDE-LIMA-01",
            name = "Sede Central San Isidro",
            address = "Av. Javier Prado 1234, Lima",
            phone = "+5114223344"
        )

        coEvery {
            coreRepository.createBranch("w1", "SEDE-LIMA-01", "Sede Central San Isidro", "Av. Javier Prado 1234, Lima", "+5114223344")
        } returns AppResult.Success(expectedBranch)

        val result = createBranchUseCase(
            workshopId = "w1",
            code = "SEDE-LIMA-01",
            name = "Sede Central San Isidro",
            address = "Av. Javier Prado 1234, Lima",
            phone = "+5114223344"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(expectedBranch, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            coreRepository.createBranch("w1", "SEDE-LIMA-01", "Sede Central San Isidro", "Av. Javier Prado 1234, Lima", "+5114223344")
        }
    }
}
