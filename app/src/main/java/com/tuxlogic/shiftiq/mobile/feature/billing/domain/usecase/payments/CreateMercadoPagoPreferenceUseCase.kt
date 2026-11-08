package com.tuxlogic.shiftiq.mobile.feature.billing.domain.usecase.payments

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentPreference
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class CreateMercadoPagoPreferenceUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        quoteId: QuoteId,
        type: VoucherType? = null,
        customerDocumentType: DocumentType? = null,
        customerDocumentNumber: String? = null,
        customerName: String? = null
    ): AppResult<PaymentPreference> {
        if (type == VoucherType.INVOICE) {
            if (customerDocumentType != DocumentType.RUC) {
                return AppResult.Failure(
                    AppError.Validation(
                        details = "customerDocumentType",
                        message = "Invoice preference requires RUC document type"
                    )
                )
            }
            val trimmedDocNumber = customerDocumentNumber?.trim()
            if (trimmedDocNumber.isNullOrBlank() || !trimmedDocNumber.matches(Regex("^[0-9]{11}$"))) {
                return AppResult.Failure(
                    AppError.Validation(
                        details = "customerDocumentNumber",
                        message = "Invoice preference requires a valid 11-digit RUC"
                    )
                )
            }
            if (customerName.isNullOrBlank()) {
                return AppResult.Failure(
                    AppError.Validation(
                        details = "customerName",
                        message = "Invoice preference requires customer name"
                    )
                )
            }
        }

        return billingRepository.createMercadoPagoPreference(
            quoteId = quoteId,
            type = type,
            customerDocumentType = customerDocumentType,
            customerDocumentNumber = customerDocumentNumber?.trim(),
            customerName = customerName?.trim()
        )
    }
}
