package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class RegisterPaymentUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        voucherId: VoucherId,
        amount: Double,
        method: PaymentMethod
    ): AppResult<Voucher> {
        if (amount <= 0.0) {
            return AppResult.Failure(
                AppError.Validation(details = "amount", message = "Payment amount must be greater than zero")
            )
        }
        return billingRepository.registerPayment(
            voucherId = voucherId,
            amount = amount,
            method = method
        )
    }
}
