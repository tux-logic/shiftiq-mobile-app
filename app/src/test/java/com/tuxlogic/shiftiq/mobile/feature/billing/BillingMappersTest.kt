package com.tuxlogic.shiftiq.mobile.feature.billing

import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.MercadoPagoPreferenceResponseDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.PaymentDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.QuoteItemDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.QuoteResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.VoucherResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteItemType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

class BillingMappersTest {

    @Test
    fun `QuoteItemDto maps correctly to Domain`() {
        val dto = QuoteItemDto(
            type = "SERVICE",
            name = "Oil Change",
            quantity = 2,
            unitPrice = 50.0,
            total = 100.0
        )
        val domain = dto.toDomain()

        assertEquals(QuoteItemType.SERVICE, domain.type)
        assertEquals("Oil Change", domain.name)
        assertEquals(2, domain.quantity)
        assertEquals(Money(50.0), domain.unitPrice)
        assertEquals(Money(100.0), domain.total)
    }

    @Test
    fun `QuoteResourceDto maps correctly to Domain including items`() {
        val quoteId = UUID.randomUUID().toString()
        val woId = UUID.randomUUID().toString()
        val branchId = UUID.randomUUID().toString()

        val dto = QuoteResourceDto(
            id = quoteId,
            workOrderId = woId,
            branchId = branchId,
            subtotalAmount = 200.0,
            discountPercentage = 10.0,
            totalAmount = 180.0,
            status = "APPROVED",
            items = listOf(
                QuoteItemDto("PRODUCT", "Brake Pad", 1, 180.0, 180.0)
            )
        )
        val domain = dto.toDomain()

        assertEquals(quoteId, domain.id.toString())
        assertEquals(woId, domain.workOrderId.toString())
        assertEquals(branchId, domain.branchId.toString())
        assertEquals(Money(200.0), domain.subtotalAmount)
        assertEquals(10.0, domain.discountPercentage, 0.001)
        assertEquals(Money(180.0), domain.totalAmount)
        assertEquals(QuoteStatus.APPROVED, domain.status)
        assertEquals(1, domain.items.size)
        assertEquals(QuoteItemType.PRODUCT, domain.items[0].type)
    }

    @Test
    fun `PaymentDto maps correctly to Domain`() {
        val dto = PaymentDto(
            id = "pay-100",
            amount = 180.0,
            method = "CREDIT_CARD",
            paidAt = "2026-11-07T12:00:00Z"
        )
        val domain = dto.toDomain()

        assertEquals("pay-100", domain.id.value)
        assertEquals(Money(180.0), domain.amount)
        assertEquals(PaymentMethod.CREDIT_CARD, domain.method)
        assertEquals("2026-11-07T12:00:00Z", domain.paidAt)
    }

    @Test
    fun `VoucherResourceDto maps correctly to Domain with default fallback calculations`() {
        val voucherId = UUID.randomUUID().toString()
        val quoteId = UUID.randomUUID().toString()

        val dto = VoucherResourceDto(
            id = voucherId,
            quoteId = quoteId,
            type = "INVOICE",
            customerDocumentType = "RUC",
            customerDocumentNumber = "20123456789",
            customerName = "Empresa S.A.C.",
            totalAmount = 300.0,
            status = "PAID",
            externalInvoiceId = "fct-01",
            series = "F001",
            correlative = "00000001",
            fiscalNumber = "F001-00000001",
            pdfUrl = "https://example.com/invoice.pdf",
            payments = listOf(
                PaymentDto("p1", 300.0, "BANK_TRANSFER", "2026-11-07T12:00:00Z")
            ),
            totalPaid = 300.0,
            balance = 0.0
        )
        val domain = dto.toDomain()

        assertEquals(voucherId, domain.id.toString())
        assertEquals(quoteId, domain.quoteId.toString())
        assertEquals(VoucherType.INVOICE, domain.type)
        assertEquals(DocumentType.RUC, domain.customerDocumentType)
        assertEquals("20123456789", domain.customerDocumentNumber)
        assertEquals("Empresa S.A.C.", domain.customerName)
        assertEquals(Money(300.0), domain.totalAmount)
        assertEquals(VoucherStatus.PAID, domain.status)
        assertEquals("F001-00000001", domain.fiscalNumber)
        assertEquals(1, domain.payments.size)
        assertEquals(Money(300.0), domain.totalPaid)
        assertEquals(Money(0.0), domain.balance)
    }

    @Test
    fun `MercadoPagoPreferenceResponseDto maps correctly to Domain`() {
        val dto = MercadoPagoPreferenceResponseDto(
            preferenceId = "pref-abc",
            initPoint = "https://mp.com/init",
            sandboxInitPoint = "https://mp.com/sandbox",
            amount = 150.0,
            currency = "PEN",
            externalReference = "ref-123"
        )
        val domain = dto.toDomain()

        assertEquals("pref-abc", domain.preferenceId)
        assertEquals("https://mp.com/init", domain.initPoint)
        assertEquals("https://mp.com/sandbox", domain.sandboxInitPoint)
        assertEquals(Money(150.0), domain.amount)
        assertEquals("PEN", domain.currency)
        assertEquals("ref-123", domain.externalReference)
    }
}
