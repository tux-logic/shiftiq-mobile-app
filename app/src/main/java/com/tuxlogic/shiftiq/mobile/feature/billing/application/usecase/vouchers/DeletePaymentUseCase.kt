package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class DeletePaymentUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        voucherId: VoucherId,
        paymentId: PaymentId
    ): AppResult<Unit> {
        return billingRepository.deletePayment(
            voucherId = voucherId,
            paymentId = paymentId
        )
    }
}
