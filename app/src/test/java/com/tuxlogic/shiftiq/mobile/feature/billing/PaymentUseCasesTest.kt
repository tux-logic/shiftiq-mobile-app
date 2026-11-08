package com.tuxlogic.shiftiq.mobile.feature.billing

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentPreference
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.payments.ConfirmMercadoPagoCheckoutUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.payments.CreateMercadoPagoPreferenceUseCase
import com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.payments.DirectCheckoutUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PaymentUseCasesTest {

    private val billingRepository: BillingRepository = mockk()

    private lateinit var directCheckoutUseCase: DirectCheckoutUseCase
    private lateinit var createMercadoPagoPreferenceUseCase: CreateMercadoPagoPreferenceUseCase
    private lateinit var confirmMercadoPagoCheckoutUseCase: ConfirmMercadoPagoCheckoutUseCase

    private val quoteId = QuoteId(UUID.randomUUID())
    private val voucherId = VoucherId(UUID.randomUUID())

    private val samplePaidVoucher = Voucher(
        id = voucherId,
        quoteId = quoteId,
        type = VoucherType.RECEIPT,
        customerDocumentType = DocumentType.DNI,
        customerDocumentNumber = "48765432",
        customerName = "Maria Fernandez",
        totalAmount = Money(225.0),
        status = VoucherStatus.PAID,
        series = "B001",
        correlative = "00001234",
        fiscalNumber = "B001-00001234",
        pdfUrl = "https://factos.example.com/pdf",
        totalPaid = Money(225.0),
        balance = Money(0.0)
    )

    private val samplePreference = PaymentPreference(
        preferenceId = "pref-12345",
        initPoint = "https://www.mercadopago.com.pe/checkout/v1/redirect?pref_id=pref-12345",
        sandboxInitPoint = "https://sandbox.mercadopago.com.pe/checkout/v1/redirect?pref_id=pref-12345",
        amount = Money(225.0),
        currency = "PEN",
        externalReference = quoteId.toString()
    )

    @Before
    fun setUp() {
        directCheckoutUseCase = DirectCheckoutUseCase(billingRepository)
        createMercadoPagoPreferenceUseCase = CreateMercadoPagoPreferenceUseCase(billingRepository)
        confirmMercadoPagoCheckoutUseCase = ConfirmMercadoPagoCheckoutUseCase(billingRepository)
    }

    @Test
    fun `DirectCheckoutUseCase with blank customer name should return validation failure`() = runTest {
        val result = directCheckoutUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "   ",
            method = PaymentMethod.CASH
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.directCheckout(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `DirectCheckoutUseCase with valid input should complete checkout`() = runTest {
        coEvery {
            billingRepository.directCheckout(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez",
                PaymentMethod.CASH
            )
        } returns AppResult.Success(samplePaidVoucher)

        val result = directCheckoutUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez",
            method = PaymentMethod.CASH
        )

        assertTrue(result is AppResult.Success)
        assertEquals(VoucherStatus.PAID, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) {
            billingRepository.directCheckout(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez",
                PaymentMethod.CASH
            )
        }
    }

    @Test
    fun `CreateMercadoPagoPreferenceUseCase with invalid Invoice RUC should return validation failure`() = runTest {
        val result = createMercadoPagoPreferenceUseCase(
            quoteId = quoteId,
            type = VoucherType.INVOICE,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "12345678",
            customerName = "Empresa S.A.C."
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.createMercadoPagoPreference(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `CreateMercadoPagoPreferenceUseCase with valid parameters should create preference`() = runTest {
        coEvery {
            billingRepository.createMercadoPagoPreference(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez"
            )
        } returns AppResult.Success(samplePreference)

        val result = createMercadoPagoPreferenceUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(samplePreference, (result as AppResult.Success).data)
        coVerify(exactly = 1) {
            billingRepository.createMercadoPagoPreference(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez"
            )
        }
    }

    @Test
    fun `ConfirmMercadoPagoCheckoutUseCase with empty paymentId should return validation failure`() = runTest {
        val result = confirmMercadoPagoCheckoutUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez",
            paymentId = "   "
        )

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { billingRepository.confirmMercadoPagoCheckout(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `ConfirmMercadoPagoCheckoutUseCase with valid parameters should confirm checkout`() = runTest {
        coEvery {
            billingRepository.confirmMercadoPagoCheckout(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez",
                "mp-pay-999"
            )
        } returns AppResult.Success(samplePaidVoucher)

        val result = confirmMercadoPagoCheckoutUseCase(
            quoteId = quoteId,
            type = VoucherType.RECEIPT,
            customerDocumentType = DocumentType.DNI,
            customerDocumentNumber = "48765432",
            customerName = "Maria Fernandez",
            paymentId = "mp-pay-999"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(VoucherStatus.PAID, (result as AppResult.Success).data.status)
        coVerify(exactly = 1) {
            billingRepository.confirmMercadoPagoCheckout(
                quoteId,
                VoucherType.RECEIPT,
                DocumentType.DNI,
                "48765432",
                "Maria Fernandez",
                "mp-pay-999"
            )
        }
    }
}
