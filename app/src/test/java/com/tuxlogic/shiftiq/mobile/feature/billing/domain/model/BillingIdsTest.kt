package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.UUID

class BillingIdsTest {

    @Test
    fun `QuoteId should parse valid UUID and string representation`() {
        val uuid = UUID.randomUUID()
        val quoteIdFromUuid = QuoteId(uuid)
        val quoteIdFromStr = QuoteId(uuid.toString())

        assertEquals(uuid, quoteIdFromUuid.value)
        assertEquals(quoteIdFromUuid, quoteIdFromStr)
        assertEquals(uuid.toString(), quoteIdFromStr.toString())
    }

    @Test
    fun `VoucherId should parse valid UUID and string representation`() {
        val uuid = UUID.randomUUID()
        val voucherIdFromUuid = VoucherId(uuid)
        val voucherIdFromStr = VoucherId(uuid.toString())

        assertEquals(uuid, voucherIdFromUuid.value)
        assertEquals(voucherIdFromUuid, voucherIdFromStr)
        assertEquals(uuid.toString(), voucherIdFromStr.toString())
    }

    @Test
    fun `PaymentId should allow valid non-blank string and reject blank`() {
        val paymentId = PaymentId("payment-12345")
        assertEquals("payment-12345", paymentId.value)
        assertEquals("payment-12345", paymentId.toString())

        assertThrows(IllegalArgumentException::class.java) {
            PaymentId("")
        }
        assertThrows(IllegalArgumentException::class.java) {
            PaymentId("   ")
        }
    }
}
