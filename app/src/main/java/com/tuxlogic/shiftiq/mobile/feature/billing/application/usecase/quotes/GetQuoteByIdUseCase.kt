package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class GetQuoteByIdUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(id: QuoteId): AppResult<Quote> {
        return billingRepository.getQuoteById(id = id)
    }
}
