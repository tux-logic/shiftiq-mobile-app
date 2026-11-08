package com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.MercadoPagoPreferenceResponseDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.PaymentDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.QuoteItemDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.QuoteResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.VoucherResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Payment
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentPreference
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteItem
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteItemType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherStatus
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId

fun QuoteItemDto.toDomain(): QuoteItem = QuoteItem(
    type = runCatching { QuoteItemType.valueOf(type.uppercase()) }.getOrDefault(QuoteItemType.SERVICE),
    name = name,
    quantity = quantity,
    unitPrice = Money(unitPrice),
    total = Money(total)
)

fun QuoteResourceDto.toDomain(): Quote = Quote(
    id = QuoteId(id),
    workOrderId = WorkOrderId(workOrderId),
    branchId = BranchId(branchId),
    subtotalAmount = Money(subtotalAmount),
    discountPercentage = discountPercentage,
    totalAmount = Money(totalAmount),
    status = runCatching { QuoteStatus.valueOf(status.uppercase()) }.getOrDefault(QuoteStatus.DRAFT),
    items = items?.map { it.toDomain() } ?: emptyList()
)

fun PaymentDto.toDomain(): Payment = Payment(
    id = PaymentId(id),
    amount = Money(amount),
    method = runCatching { PaymentMethod.valueOf(method.uppercase()) }.getOrDefault(PaymentMethod.CASH),
    paidAt = paidAt
)

fun VoucherResourceDto.toDomain(): Voucher {
    val total = Money(totalAmount)
    val paid = Money(totalPaid ?: 0.0)
    val remainingBalance = balance?.let { Money(it) } ?: (total - paid)

    return Voucher(
        id = VoucherId(id),
        quoteId = QuoteId(quoteId),
        type = runCatching { VoucherType.valueOf(type.uppercase()) }.getOrDefault(VoucherType.RECEIPT),
        customerDocumentType = runCatching { DocumentType.valueOf(customerDocumentType.uppercase()) }.getOrDefault(DocumentType.DNI),
        customerDocumentNumber = customerDocumentNumber,
        customerName = customerName,
        totalAmount = total,
        status = runCatching { VoucherStatus.valueOf(status.uppercase()) }.getOrDefault(VoucherStatus.PENDING),
        externalInvoiceId = externalInvoiceId,
        series = series,
        correlative = correlative,
        fiscalNumber = fiscalNumber,
        pdfUrl = pdfUrl,
        payments = payments?.map { it.toDomain() } ?: emptyList(),
        totalPaid = paid,
        balance = remainingBalance
    )
}

fun MercadoPagoPreferenceResponseDto.toDomain(): PaymentPreference = PaymentPreference(
    preferenceId = preferenceId,
    initPoint = initPoint,
    sandboxInitPoint = sandboxInitPoint,
    amount = Money(amount),
    currency = currency,
    externalReference = externalReference
)
