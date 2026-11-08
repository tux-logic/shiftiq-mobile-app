package com.tuxlogic.shiftiq.mobile.feature.billing

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Payment
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.DeletePaymentUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.EmitVoucherUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVoucherByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.GetVouchersUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers.RegisterPaymentUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class VoucherUseCasesTest {

    private val billingRepository: BillingRepository = mockk()

    private lateinit var getVouchersUseCase: GetVouchersUseCase
    private lateinit var getVoucherByIdUseCase: GetVoucherByIdUseCase
    private lateinit var emitVoucherUseCase: EmitVoucherUseCase
    private lateinit var registerPaymentUseCase: RegisterPaymentUseCase
    private lateinit var deletePaymentUseCase: DeletePaymentUseCase

    private val branchId = BranchId(UUID.randomUUID())
    private val quoteId = QuoteId(UUID.randomUUID())
    private val voucherId = VoucherId(UUID.randomUUID())
    private val paymentId = PaymentId("pay-1")

    private val sampleVoucher = Voucher(
        id = voucherId,
        quoteId = quoteId,
        type = VoucherType.RECEIPT,
        customerDocumentType = DocumentType.DNI,
        customerDocumentNumber = "48765432",
        customerName = "Maria Fernandez",
        totalAmount = Money(225.0),
        status = VoucherStatus.PENDING,
        series = "B001",
        correlative = "00001234",
        fiscalNumber = "B001-00001234",
        pdfUrl = "https://factos.example.com/pdf",
        payments = emptyList(),
        totalPaid = Money(0.0),
        balance = Money(225.0)
    )

    @Before
    fun setUp() {
        getVouchersUseCase = GetVouchersUseCase(billingRepository)
        getVoucherByIdUseCase = GetVoucherByIdUseCase(billingRepository)
        emitVoucherUseCase = EmitVoucherUseCase(billingRepository)
        registerPaymentUseCase = RegisterPaymentUseCase(billingRepository)
        deletePaymentUseCase = DeletePaymentUseCase(billingRepository)
    }

    @Test
    fun `GetVouchersUseCase should return vouchers from repository`() = runTest {
        coEvery { billingRepository.getVouchers(branchId) } returns AppResult.Success(listOf(sampleVoucher))

        val result = getVouchersUseCase(branchId)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(sampleVoucher), (result as AppResult.Success).data)
        coVerify(exactly = 1) { billingRepository.getVouchers(branchId) }
    }

    @Test
    fun `GetVoucherByIdUseCase should return voucher from repository`() = runTest {
        coEvery { billingRepository.getVoucherById(voucherId) } returns AppResult.Success(sampleVoucher)

        val result = getVoucherByIdUseCase(voucherId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleVoucher, (result as AppResult.Success).data)
        coVerify(exactly = 1) { billingRepository.getVoucherById(voucherId) }
    }

    @Test
    fun `EmitVoucherUseCase with invalid customer name should return validation failure`() = runTest {
        val result = emitVoucherUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "   "
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.emitVoucher(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `EmitVoucherUseCase with Invoice and non-RUC document should return validation failure`() = runTest {
        val result = emitVoucherUseCase(
            quoteId = quoteId,
            type = VoucherType.INVOICE,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Empresa S.A.C."
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.emitVoucher(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `EmitVoucherUseCase with invalid DNI length should return validation failure`() = runTest {
        val result = emitVoucherUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "12345",
            customerName = "Maria Fernandez"
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.emitVoucher(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `EmitVoucherUseCase with valid parameters should emit voucher successfully`() = runTest {
        coEvery {
            billingRepository.emitVoucher(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez"
            )
        } returns AppResult.Success(sampleVoucher)

        val result = emitVoucherUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(sampleVoucher, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            billingRepository.emitVoucher(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez"
            )
        }
    }

    @Test
    fun `RegisterPaymentUseCase with invalid amount should return validation failure`() = runTest {
        val result = registerPaymentUseCase(voucherId, 0.0, PaymentMethod.CASH)

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.registerPayment(any(), any(), any()) }
    }

    @Test
    fun `RegisterPaymentUseCase with valid amount should register payment`() = runTest {
        val paidVoucher = sampleVoucher.copy(
            status = VoucherStatus.PAID,
            totalPaid = Money(225.0),
            balance = Money(0.0),
            payments = listOf(Payment(paymentId, Money(225.0), PaymentMethod.CASH, "2026-10-15T15:00:00Z"))
        )
        coEvery { billingRepository.registerPayment(voucherId, 225.0, PaymentMethod.CASH) } returns AppResult.Success(paidVoucher)

        val result = registerPaymentUseCase(voucherId, 225.0, PaymentMethod.CASH)

        assertTrue(result is AppResult.Success)
        assertEquals(VoucherStatus.PAID, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) { billingRepository.registerPayment(voucherId, 225.0, PaymentMethod.CASH) }
    }

    @Test
    fun `DeletePaymentUseCase should call repository deletePayment`() = runTest {
        coEvery { billingRepository.deletePayment(voucherId, paymentId) } returns AppResult.Success(Unit)

        val result = deletePaymentUseCase(voucherId, paymentId)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { billingRepository.deletePayment(voucherId, paymentId) }
    }
}
