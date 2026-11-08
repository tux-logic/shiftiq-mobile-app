package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class UpdateQuoteDiscountUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        id: QuoteId,
        discountPercentage: Double
    ): AppResult<Quote> {
        if (discountPercentage < 0.0 || discountPercentage > 100.0) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "discountPercentage",
                    message = "Discount percentage must be between 0 and 100"
                )
            )
        }
        return billingRepository.updateQuoteDiscount(
            id = id,
            discountPercentage = discountPercentage
        )
    }
}
