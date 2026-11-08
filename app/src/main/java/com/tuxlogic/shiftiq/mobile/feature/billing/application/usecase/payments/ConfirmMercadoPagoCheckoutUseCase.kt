package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.payments

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class ConfirmMercadoPagoCheckoutUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String,
        paymentId: String
    ): AppResult<Voucher> {
        val trimmedPaymentId = paymentId.trim()
        if (trimmedPaymentId.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "paymentId", message = "Mercado Pago payment ID is required")
            )
        }

        val trimmedName = customerName.trim()
        if (trimmedName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "customerName", message = "Customer name is required")
            )
        }

        val trimmedDocNumber = customerDocumentNumber.trim()
        if (type == VoucherType.INVOICE && customerDocumentType != DocumentType.RUC) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "customerDocumentType",
                    message = "Invoices require a valid RUC document type"
                )
            )
        }

        when (customerDocumentType) {
            DocumentType.DNI -> {
                if (!trimmedDocNumber.matches(Regex("^[0-9]{8}$"))) {
                    return AppResult.Failure(
                        AppError.Validation(
                            details = "customerDocumentNumber",
                            message = "DNI must be exactly 8 digits"
                        )
                    )
                }
            }
            DocumentType.RUC -> {
                if (!trimmedDocNumber.matches(Regex("^[0-9]{11}$"))) {
                    return AppResult.Failure(
                        AppError.Validation(
                            details = "customerDocumentNumber",
                            message = "RUC must be exactly 11 digits"
                        )
                    )
                }
            }
        }

        return billingRepository.confirmMercadoPagoCheckout(
            quoteId = quoteId,
            type = type,
            customerDocumentType = customerDocumentType,
            customerDocumentNumber = trimmedDocNumber,
            customerName = trimmedName,
            paymentId = trimmedPaymentId
        )
    }
}
