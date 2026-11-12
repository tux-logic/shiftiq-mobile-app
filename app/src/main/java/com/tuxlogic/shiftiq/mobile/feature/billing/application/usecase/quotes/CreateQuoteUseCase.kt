package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.quotes

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import javax.inject.Inject

class CreateQuoteUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        branchId: BranchId,
        discountPercentage: Double = 0.0
    ): AppResult<Quote> {
        if (discountPercentage < 0.0 || discountPercentage > 100.0) {
            return AppResult.Failure(
                AppError.Validation(
                    details = "discountPercentage",
                    message = "Discount percentage must be between 0 and 100"
                )
            )
        }
        return billingRepository.createQuote(
            workOrderId = workOrderId,
            branchId = branchId,
            discountPercentage = discountPercentage
        )
    }
}
