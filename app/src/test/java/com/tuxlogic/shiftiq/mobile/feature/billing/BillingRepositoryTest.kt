package com.tuxlogic.shiftiq.mobile.feature.billing

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.api.BillingApiService
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.MercadoPagoPreferenceResponseDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.QuoteItemDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.QuoteResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.VoucherResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.repository.BillingRepositoryImpl
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class BillingRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val billingApiService: BillingApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk()

    private lateinit var repository: BillingRepositoryImpl

    private val branchUuid = UUID.randomUUID()
    private val branchId = BranchId(branchUuid)
    private val quoteUuid = UUID.randomUUID()
    private val quoteId = QuoteId(quoteUuid)
    private val workOrderUuid = UUID.randomUUID()
    private val workOrderId = WorkOrderId(workOrderUuid)
    private val voucherUuid = UUID.randomUUID()
    private val voucherId = VoucherId(voucherUuid)

    private val sampleQuoteDto = QuoteResourceDto(
        id = quoteUuid.toString(),
        workOrderId = workOrderUuid.toString(),
        branchId = branchUuid.toString(),
        subtotalAmount = 250.0,
        discountPercentage = 10.0,
        totalAmount = 225.0,
        status = "DRAFT",
        items = listOf(
            QuoteItemDto("SERVICE", "Brake pad replacement", 1, 80.0, 80.0)
        )
    )

    private val sampleVoucherDto = VoucherResourceDto(
        id = voucherUuid.toString(),
        quoteId = quoteUuid.toString(),
        type = "RECEIPT",
        customerDocumentType = "DNI",
        customerDocumentNumber = "48765432",
        customerName = "Maria Fernandez",
        totalAmount = 225.0,
        status = "PENDING",
        series = "B001",
        correlative = "00001234",
        fiscalNumber = "B001-00001234",
        pdfUrl = "https://factos.example.com/pdf",
        payments = emptyList(),
        totalPaid = 0.0,
        balance = 225.0
    )

    @Before
    fun setUp() {
        repository = BillingRepositoryImpl(billingApiService, sessionDataStore, dispatchers)
    }

    @Test
    fun `getQuotes with explicit branchId returns domain quotes`() = runTest(testDispatcher) {
        coEvery { billingApiService.getQuotes(branchUuid.toString()) } returns Response.success(listOf(sampleQuoteDto))

        val result = repository.getQuotes(branchId)

        assertTrue(result is AppResult.Success)
        val quotes = (result as AppResult.Success).data
        assertEquals(1, quotes.size)
        assertEquals(quoteId, quotes[0].id)
        assertEquals(Money(225.0), quotes[0].totalAmount)
        coVerify(exactly = 1) { billingApiService.getQuotes(branchUuid.toString()) }
    }

    @Test
    fun `getQuotes fallback to sessionDataStore when branchId is null`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(branchId)
        coEvery { billingApiService.getQuotes(branchUuid.toString()) } returns Response.success(listOf(sampleQuoteDto))

        val result = repository.getQuotes(null)

        assertTrue(result is AppResult.Success)
        val quotes = (result as AppResult.Success).data
        assertEquals(1, quotes.size)
        coVerify(exactly = 1) { billingApiService.getQuotes(branchUuid.toString()) }
    }

    @Test
    fun `getQuotes returns failure when branchId is null and no active branch in session`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.activeBranchId } returns flowOf(null)

        val result = repository.getQuotes(null)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingApiService.getQuotes(any()) }
    }

    @Test
    fun `getQuoteById returns mapped Quote`() = runTest(testDispatcher) {
        coEvery { billingApiService.getQuoteById(quoteUuid.toString()) } returns Response.success(sampleQuoteDto)

        val result = repository.getQuoteById(quoteId)

        assertTrue(result is AppResult.Success)
        val quote = (result as AppResult.Success).data
        assertEquals(quoteId, quote.id)
        assertEquals(QuoteStatus.DRAFT, quote.status)
        assertEquals(1, quote.items.size)
        coVerify(exactly = 1) { billingApiService.getQuoteById(quoteUuid.toString()) }
    }

    @Test
    fun `createQuote sends correct request and returns created quote`() = runTest(testDispatcher) {
        coEvery { billingApiService.createQuote(any()) } returns Response.success(sampleQuoteDto)

        val result = repository.createQuote(workOrderId, branchId, 10.0)

        assertTrue(result is AppResult.Success)
        assertEquals(quoteId, (result as AppResult.Success).data.id)
        coVerify(exactly = 1) { billingApiService.createQuote(match {
            it.workOrderId == workOrderUuid.toString() &&
            it.branchId == branchUuid.toString() &&
            it.discountPercentage == 10.0
        }) }
    }

    @Test
    fun `updateQuoteDiscount sends request and returns updated quote`() = runTest(testDispatcher) {
        val updatedDto = sampleQuoteDto.copy(discountPercentage = 15.0, totalAmount = 212.5)
        coEvery { billingApiService.updateQuoteDiscount(quoteUuid.toString(), any()) } returns Response.success(updatedDto)

        val result = repository.updateQuoteDiscount(quoteId, 15.0)

        assertTrue(result is AppResult.Success)
        val quote = (result as AppResult.Success).data
        assertEquals(15.0, quote.discountPercentage, 0.001)
        assertEquals(Money(212.5), quote.totalAmount)
        coVerify(exactly = 1) { billingApiService.updateQuoteDiscount(quoteUuid.toString(), match { it.discountPercentage == 15.0 }) }
    }

    @Test
    fun `approveQuote sends approval request and returns approved quote`() = runTest(testDispatcher) {
        val approvedDto = sampleQuoteDto.copy(status = "APPROVED")
        coEvery { billingApiService.approveQuote(quoteUuid.toString()) } returns Response.success(approvedDto)

        val result = repository.approveQuote(quoteId)

        assertTrue(result is AppResult.Success)
        assertEquals(QuoteStatus.APPROVED, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingApiService.approveQuote(quoteUuid.toString()) }
    }

    @Test
    fun `cancelQuote sends cancellation request and returns canceled quote`() = runTest(testDispatcher) {
        val canceledDto = sampleQuoteDto.copy(status = "CANCELED")
        coEvery { billingApiService.cancelQuote(quoteUuid.toString()) } returns Response.success(canceledDto)

        val result = repository.cancelQuote(quoteId)

        assertTrue(result is AppResult.Success)
        assertEquals(QuoteStatus.CANCELED, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingApiService.cancelQuote(quoteUuid.toString()) }
    }

    @Test
    fun `getVouchers with explicit branchId returns domain vouchers`() = runTest(testDispatcher) {
        coEvery { billingApiService.getVouchers(branchUuid.toString()) } returns Response.success(listOf(sampleVoucherDto))

        val result = repository.getVouchers(branchId)

        assertTrue(result is AppResult.Success)
        val vouchers = (result as AppResult.Success).data
        assertEquals(1, vouchers.size)
        assertEquals(voucherId, vouchers[0].id)
        assertEquals("B001-00001234", vouchers[0].fiscalNumber)
        coVerify(exactly = 1) { billingApiService.getVouchers(branchUuid.toString()) }
    }

    @Test
    fun `getVoucherById returns mapped Voucher`() = runTest(testDispatcher) {
        coEvery { billingApiService.getVoucherById(voucherUuid.toString()) } returns Response.success(sampleVoucherDto)

        val result = repository.getVoucherById(voucherId)

        assertTrue(result is AppResult.Success)
        val voucher = (result as AppResult.Success).data
        assertEquals(voucherId, voucher.id)
        assertEquals(VoucherStatus.PENDING, voucher.status)
        coVerify(exactly = 1) { billingApiService.getVoucherById(voucherUuid.toString()) }
    }

    @Test
    fun `emitVoucher sends request and returns emitted voucher`() = runTest(testDispatcher) {
        coEvery { billingApiService.emitVoucher(any()) } returns Response.success(sampleVoucherDto)

        val result = repository.emitVoucher(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(voucherId, (result as AppResult.Success).data.id)
        coVerify(exactly = 1) { billingApiService.emitVoucher(match {
            it.quoteId == quoteUuid.toString() &&
            it.type == "RECEIPT" &&
            it.customerDocumentType == "DNI" &&
            it.customerDocumentNumber == "48765432" &&
            it.customerName == "Maria Fernandez"
        }) }
    }

    @Test
    fun `registerPayment sends payment request and returns updated voucher`() = runTest(testDispatcher) {
        val paidDto = sampleVoucherDto.copy(status = "PAID", totalPaid = 225.0, balance = 0.0)
        coEvery { billingApiService.registerPayment(voucherUuid.toString(), any()) } returns Response.success(paidDto)

        val result = repository.registerPayment(voucherId, 225.0, PaymentMethod.CASH)

        assertTrue(result is AppResult.Success)
        assertEquals(VoucherStatus.PAID, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingApiService.registerPayment(voucherUuid.toString(), match {
            it.amount == 225.0 && it.method == "CASH"
        }) }
    }

    @Test
    fun `deletePayment deletes payment on voucher`() = runTest(testDispatcher) {
        coEvery { billingApiService.deletePayment(voucherUuid.toString(), "pay-1") } returns Response.success(Unit)

        val result = repository.deletePayment(voucherId, PaymentId("pay-1"))

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { billingApiService.deletePayment(voucherUuid.toString(), "pay-1") }
    }

    @Test
    fun `directCheckout sends atomic request and returns paid voucher`() = runTest(testDispatcher) {
        val paidDto = sampleVoucherDto.copy(status = "PAID", totalPaid = 225.0, balance = 0.0)
        coEvery { billingApiService.directCheckout(any()) } returns Response.success(paidDto)

        val result = repository.directCheckout(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez",
            method = PaymentMethod.CASH
        )

        assertTrue(result is AppResult.Success)
        assertEquals(VoucherStatus.PAID, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingApiService.directCheckout(match {
            it.quoteId == quoteUuid.toString() &&
            it.method == "CASH"
        }) }
    }

    @Test
    fun `createMercadoPagoPreference creates checkout preference`() = runTest(testDispatcher) {
        val prefDto = MercadoPagoPreferenceResponseDto(
            preferenceId = "pref-123",
            initPoint = "https://mp.com/init",
            sandboxInitPoint = "https://mp.com/sandbox",
            amount = 225.0,
            currency = "PEN",
            externalReference = quoteUuid.toString()
        )
        coEvery { billingApiService.createMercadoPagoPreference(any()) } returns Response.success(prefDto)

        val result = repository.createMercadoPagoPreference(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez"
        )

        assertTrue(result is AppResult.Success)
        assertEquals("pref-123", (result as AppResult.Success).data.preferenceId)
        coVerify(exactly = 1) { billingApiService.createMercadoPagoPreference(match {
            it.quoteId == quoteUuid.toString()
        }) }
    }

    @Test
    fun `confirmMercadoPagoCheckout confirms payment and returns voucher`() = runTest(testDispatcher) {
        val paidDto = sampleVoucherDto.copy(status = "PAID", totalPaid = 225.0, balance = 0.0)
        coEvery { billingApiService.confirmMercadoPagoCheckout(any()) } returns Response.success(paidDto)

        val result = repository.confirmMercadoPagoCheckout(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez",
            paymentId = "mp-pay-999"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(VoucherStatus.PAID, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingApiService.confirmMercadoPagoCheckout(match {
            it.paymentId == "mp-pay-999"
        }) }
    }
}
