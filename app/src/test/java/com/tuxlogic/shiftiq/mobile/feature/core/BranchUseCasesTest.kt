package com.tuxlogic.shiftiq.mobile.feature.core

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.BranchSubscription
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.AssignBranchSubscriptionUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CancelBranchSubscriptionUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateBranchUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchesUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.SetActiveBranchUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateBranchUseCase
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
    private lateinit var getBranchByIdUseCase: GetBranchByIdUseCase
    private lateinit var createBranchUseCase: CreateBranchUseCase
    private lateinit var updateBranchUseCase: UpdateBranchUseCase
    private lateinit var setActiveBranchUseCase: SetActiveBranchUseCase
    private lateinit var assignBranchSubscriptionUseCase: AssignBranchSubscriptionUseCase
    private lateinit var cancelBranchSubscriptionUseCase: CancelBranchSubscriptionUseCase

    @Before
    fun setUp() {
        getBranchesUseCase = GetBranchesUseCase(coreRepository)
        getBranchByIdUseCase = GetBranchByIdUseCase(coreRepository)
        createBranchUseCase = CreateBranchUseCase(coreRepository)
        updateBranchUseCase = UpdateBranchUseCase(coreRepository)
        setActiveBranchUseCase = SetActiveBranchUseCase(coreRepository)
        assignBranchSubscriptionUseCase = AssignBranchSubscriptionUseCase(coreRepository)
        cancelBranchSubscriptionUseCase = CancelBranchSubscriptionUseCase(coreRepository)
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

    @Test
    fun `updateBranch with valid data calls repository`() = runTest {
        val expectedBranch = Branch(
            id = BranchId("b1"),
            workshopId = "w1",
            code = "SEDE-LIMA-01",
            name = "Sede Central Updated",
            address = "Av. Javier Prado 1234, Lima",
            phone = "+5114223344"
        )
        coEvery {
            coreRepository.updateBranch("b1", "SEDE-LIMA-01", "Sede Central Updated", "Av. Javier Prado 1234, Lima", "+5114223344")
        } returns AppResult.Success(expectedBranch)

        val result = updateBranchUseCase("b1", "SEDE-LIMA-01", "Sede Central Updated", "Av. Javier Prado 1234, Lima", "+5114223344")
        assertTrue(result is AppResult.Success)
        assertEquals(expectedBranch, (result as AppResult.Success).data)
    }

    @Test
    fun `assignBranchSubscription with valid card data calls repository`() = runTest {
        val expectedSubscription = BranchSubscription(
            id = "sub-1",
            branchId = BranchId("b1"),
            planId = "plan-pro",
            billingCycle = "MONTHLY",
            status = "ACTIVE",
            startDate = "2026-10-01T00:00:00Z",
            endDate = "2026-11-01T00:00:00Z"
        )
        coEvery {
            coreRepository.assignSubscription("b1", "plan-pro", "MONTHLY", "4532015698451234", "Carlos Mendoza", "12/28", "123")
        } returns AppResult.Success(expectedSubscription)

        val result = assignBranchSubscriptionUseCase("b1", "plan-pro", "MONTHLY", "4532015698451234", "Carlos Mendoza", "12/28", "123")
        assertTrue(result is AppResult.Success)
        assertEquals(expectedSubscription, (result as AppResult.Success).data)
    }

    @Test
    fun `cancelBranchSubscription calls repository successfully`() = runTest {
        coEvery { coreRepository.cancelSubscription("b1") } returns AppResult.Success(Unit)

        val result = cancelBranchSubscriptionUseCase("b1")
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { coreRepository.cancelSubscription("b1") }
    }
}
