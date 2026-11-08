package com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentPreference
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId

interface BillingRepository {

    suspend fun getQuotes(branchId: BranchId? = null): AppResult<List<Quote>>

    suspend fun getQuoteById(id: QuoteId): AppResult<Quote>

    suspend fun createQuote(
        workOrderId: WorkOrderId,
        branchId: BranchId,
        discountPercentage: Double = 0.0
    ): AppResult<Quote>

    suspend fun updateQuoteDiscount(
        id: QuoteId,
        discountPercentage: Double
    ): AppResult<Quote>

    suspend fun approveQuote(id: QuoteId): AppResult<Quote>

    suspend fun cancelQuote(id: QuoteId): AppResult<Quote>

    suspend fun getVouchers(branchId: BranchId? = null): AppResult<List<Voucher>>

    suspend fun getVoucherById(id: VoucherId): AppResult<Voucher>

    suspend fun emitVoucher(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String
    ): AppResult<Voucher>

    suspend fun registerPayment(
        voucherId: VoucherId,
        amount: Double,
        method: PaymentMethod
    ): AppResult<Voucher>

    suspend fun deletePayment(
        voucherId: VoucherId,
        paymentId: PaymentId
    ): AppResult<Unit>

    suspend fun directCheckout(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String,
        method: PaymentMethod
    ): AppResult<Voucher>

    suspend fun createMercadoPagoPreference(
        quoteId: QuoteId,
        type: VoucherType? = null,
        customerDocumentType: DocumentType? = null,
        customerDocumentNumber: String? = null,
        customerName: String? = null
    ): AppResult<PaymentPreference>

    suspend fun confirmMercadoPagoCheckout(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String,
        paymentId: String
    ): AppResult<Voucher>
}
