package com.tuxlogic.shiftiq.mobile.feature.billing

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteItem
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteItemType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.ApproveQuoteUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.CancelQuoteUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.CreateQuoteUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.GetQuoteByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.GetQuotesUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes.UpdateQuoteDiscountUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class QuoteUseCasesTest {

    private val billingRepository: BillingRepository = mockk()

    private lateinit var getQuotesUseCase: GetQuotesUseCase
    private lateinit var getQuoteByIdUseCase: GetQuoteByIdUseCase
    private lateinit var createQuoteUseCase: CreateQuoteUseCase
    private lateinit var updateQuoteDiscountUseCase: UpdateQuoteDiscountUseCase
    private lateinit var approveQuoteUseCase: ApproveQuoteUseCase
    private lateinit var cancelQuoteUseCase: CancelQuoteUseCase

    private val branchId = BranchId(UUID.randomUUID())
    private val workOrderId = WorkOrderId(UUID.randomUUID())
    private val quoteId = QuoteId(UUID.randomUUID())

    private val sampleQuote = Quote(
        id = quoteId,
        workOrderId = workOrderId,
        branchId = branchId,
        subtotalAmount = Money(250.0),
        discountPercentage = 10.0,
        totalAmount = Money(225.0),
        status = QuoteStatus.DRAFT,
        items = listOf(
            QuoteItem(
                type = QuoteItemType.SERVICE,
                name = "Brake pad replacement",
                quantity = 1,
                unitPrice = Money(80.0),
                total = Money(80.0)
            )
        )
    )

    @Before
    fun setUp() {
        getQuotesUseCase = GetQuotesUseCase(billingRepository)
        getQuoteByIdUseCase = GetQuoteByIdUseCase(billingRepository)
        createQuoteUseCase = CreateQuoteUseCase(billingRepository)
        updateQuoteDiscountUseCase = UpdateQuoteDiscountUseCase(billingRepository)
        approveQuoteUseCase = ApproveQuoteUseCase(billingRepository)
        cancelQuoteUseCase = CancelQuoteUseCase(billingRepository)
    }

    @Test
    fun `GetQuotesUseCase should call repository and return quote list`() = runTest {
        coEvery { billingRepository.getQuotes(branchId) } returns AppResult.Success(listOf(sampleQuote))

        val result = getQuotesUseCase(branchId)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleQuote), (result as AppResult.Success).data)
        coVerify(exactly = 1) { billingRepository.getQuotes(branchId) }
    }

    @Test
    fun `GetQuoteByIdUseCase should call repository and return single quote`() = runTest {
        coEvery { billingRepository.getQuoteById(quoteId) } returns AppResult.Success(sampleQuote)

        val result = getQuoteByIdUseCase(quoteId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleQuote, (result as AppResult.Success).data)
        coVerify(exactly = 1) { billingRepository.getQuoteById(quoteId) }
    }

    @Test
    fun `CreateQuoteUseCase with invalid discount should return validation failure`() = runTest {
        val resultNegative = createQuoteUseCase(workOrderId, branchId, -5.0)
        assertTrue(resultNegative is AppResult.Failure)
        assertTrue((resultNegative as AppResult.Failure).error is AppError.Validation)

        val resultAbove100 = createQuoteUseCase(workOrderId, branchId, 105.0)
        assertTrue(resultAbove100 is AppResult.Failure)
        assertTrue((resultAbove100 as AppResult.Failure).error is AppError.Validation)

        coVerify(exactly = 0) { billingRepository.createQuote(any(), any(), any()) }
    }

    @Test
    fun `CreateQuoteUseCase with valid discount should call repository`() = runTest {
        coEvery { billingRepository.createQuote(workOrderId, branchId, 10.0) } returns AppResult.Success(sampleQuote)

        val result = createQuoteUseCase(workOrderId, branchId, 10.0)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleQuote, (result as AppResult.Success).data)
        coVerify(exactly = 1) { billingRepository.createQuote(workOrderId, branchId, 10.0) }
    }

    @Test
    fun `UpdateQuoteDiscountUseCase with invalid discount should return validation failure`() = runTest {
        val result = updateQuoteDiscountUseCase(quoteId, 150.0)
        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)

        coVerify(exactly = 0) { billingRepository.updateQuoteDiscount(any(), any()) }
    }

    @Test
    fun `UpdateQuoteDiscountUseCase with valid discount should call repository`() = runTest {
        val updatedQuote = sampleQuote.copy(discountPercentage = 15.0, totalAmount = Money(212.5))
        coEvery { billingRepository.updateQuoteDiscount(quoteId, 15.0) } returns AppResult.Success(updatedQuote)

        val result = updateQuoteDiscountUseCase(quoteId, 15.0)

        assertTrue(result is AppResult.Success)
        assertEquals(updatedQuote, (result as AppResult.Success).data)
        coVerify(exactly = 1) { billingRepository.updateQuoteDiscount(quoteId, 15.0) }
    }

    @Test
    fun `ApproveQuoteUseCase should transition quote status to APPROVED`() = runTest {
        val approvedQuote = sampleQuote.copy(status = QuoteStatus.APPROVED)
        coEvery { billingRepository.approveQuote(quoteId) } returns AppResult.Success(approvedQuote)

        val result = approveQuoteUseCase(quoteId)

        assertTrue(result is AppResult.Success)
        assertEquals(QuoteStatus.APPROVED, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingRepository.approveQuote(quoteId) }
    }

    @Test
    fun `CancelQuoteUseCase should transition quote status to CANCELED`() = runTest {
        val canceledQuote = sampleQuote.copy(status = QuoteStatus.CANCELED)
        coEvery { billingRepository.cancelQuote(quoteId) } returns AppResult.Success(canceledQuote)

        val result = cancelQuoteUseCase(quoteId)

        assertTrue(result is AppResult.Success)
        assertEquals(QuoteStatus.CANCELED, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingRepository.cancelQuote(quoteId) }
    }
}
