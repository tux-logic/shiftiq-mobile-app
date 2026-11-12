package com.tuxlogic.shiftiq.mobile.feature.billing.application.usecase.vouchers

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import javax.inject.Inject

class GetVouchersUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(branchId: BranchId? = null): AppResult<List<Voucher>> {
        return billingRepository.getVouchers(branchId = branchId)
    }
}
