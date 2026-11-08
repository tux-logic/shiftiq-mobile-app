package com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Quotes DTOs ---

data class CreateQuoteRequestDto(
    @SerializedName("workOrderId") val workOrderId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("discountPercentage") val discountPercentage: Double? = 0.0
)

data class UpdateQuoteDiscountRequestDto(
    @SerializedName("discountPercentage") val discountPercentage: Double
)

data class QuoteItemDto(
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("total") val total: Double
)

data class QuoteResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("workOrderId") val workOrderId: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("subtotalAmount") val subtotalAmount: Double,
    @SerializedName("discountPercentage") val discountPercentage: Double,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("items") val items: List<QuoteItemDto>? = null
)

// --- Vouchers & Payments DTOs ---

data class EmitVoucherRequestDto(
    @SerializedName("quoteId") val quoteId: String,
    @SerializedName("type") val type: String,
    @SerializedName("customerDocumentType") val customerDocumentType: String,
    @SerializedName("customerDocumentNumber") val customerDocumentNumber: String,
    @SerializedName("customerName") val customerName: String
)

data class PaymentDto(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("method") val method: String,
    @SerializedName("paidAt") val paidAt: String? = null
)

data class RegisterPaymentRequestDto(
    @SerializedName("amount") val amount: Double,
    @SerializedName("method") val method: String
)

data class VoucherResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("quoteId") val quoteId: String,
    @SerializedName("type") val type: String,
    @SerializedName("customerDocumentType") val customerDocumentType: String,
    @SerializedName("customerDocumentNumber") val customerDocumentNumber: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("externalInvoiceId") val externalInvoiceId: String? = null,
    @SerializedName("series") val series: String? = null,
    @SerializedName("correlative") val correlative: String? = null,
    @SerializedName("fiscalNumber") val fiscalNumber: String? = null,
    @SerializedName("pdfUrl") val pdfUrl: String? = null,
    @SerializedName("payments") val payments: List<PaymentDto>? = null,
    @SerializedName("totalPaid") val totalPaid: Double? = null,
    @SerializedName("balance") val balance: Double? = null
)

// --- Checkouts & Mercado Pago DTOs ---

data class DirectCheckoutRequestDto(
    @SerializedName("quoteId") val quoteId: String,
    @SerializedName("type") val type: String,
    @SerializedName("customerDocumentType") val customerDocumentType: String,
    @SerializedName("customerDocumentNumber") val customerDocumentNumber: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("method") val method: String
)

data class MercadoPagoPreferenceRequestDto(
    @SerializedName("quoteId") val quoteId: String,
    @SerializedName("type") val type: String? = null,
    @SerializedName("customerDocumentType") val customerDocumentType: String? = null,
    @SerializedName("customerDocumentNumber") val customerDocumentNumber: String? = null,
    @SerializedName("customerName") val customerName: String? = null
)

data class MercadoPagoPreferenceResponseDto(
    @SerializedName("preferenceId") val preferenceId: String,
    @SerializedName("initPoint") val initPoint: String,
    @SerializedName("sandboxInitPoint") val sandboxInitPoint: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("externalReference") val externalReference: String
)

data class MercadoPagoCheckoutRequestDto(
    @SerializedName("quoteId") val quoteId: String,
    @SerializedName("type") val type: String,
    @SerializedName("customerDocumentType") val customerDocumentType: String,
    @SerializedName("customerDocumentNumber") val customerDocumentNumber: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("paymentId") val paymentId: String
)
