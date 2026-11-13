package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

enum class VoucherType {
    RECEIPT,
    INVOICE
}

enum class DocumentType {
    DNI,
    RUC
}

enum class VoucherStatus {
    PENDING,
    EMITTED,
    PARTIALLY_PAID,
    PAID,
    EMISSION_FAILED,
    CANCELED
}

data class Voucher(
    val id: VoucherId,
    val quoteId: QuoteId,
    val type: VoucherType,
    val customerDocumentType: DocumentType,
    val customerDocumentNumber: String,
    val customerName: String,
    val totalAmount: Money,
    val status: VoucherStatus,
    val externalInvoiceId: String? = null,
    val series: String? = null,
    val correlative: String? = null,
    val fiscalNumber: String? = null,
    val pdfUrl: String? = null,
    val payments: List<Payment> = emptyList(),
    val totalPaid: Money = Money(0.0),
    val balance: Money = totalAmount
)
