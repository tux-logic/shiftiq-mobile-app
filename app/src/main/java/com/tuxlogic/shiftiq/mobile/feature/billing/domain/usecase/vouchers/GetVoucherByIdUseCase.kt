package com.tuxlogic.shiftiq.mobile.feature.billing.domain.usecase.vouchers

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class GetVoucherByIdUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(id: VoucherId): AppResult<Voucher> {
        return billingRepository.getVoucherById(id = id)
    }
}
