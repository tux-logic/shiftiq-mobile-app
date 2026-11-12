package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money

data class ServiceItem(
    val id: ServiceId,
    val branchId: BranchId,
    val name: String,
    val price: Money
)
