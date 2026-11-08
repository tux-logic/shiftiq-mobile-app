package com.tuxlogic.shiftiq.mobile.feature.billing.domain.usecase.quotes

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class GetQuotesUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(branchId: BranchId? = null): AppResult<List<Quote>> {
        return billingRepository.getQuotes(branchId = branchId)
    }
}
