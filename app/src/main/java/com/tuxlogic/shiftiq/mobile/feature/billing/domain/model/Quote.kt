package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId

enum class QuoteStatus {
    DRAFT,
    APPROVED,
    CANCELED
}

data class Quote(
    val id: QuoteId,
    val workOrderId: WorkOrderId,
    val branchId: BranchId,
    val subtotalAmount: Money,
    val discountPercentage: Double,
    val totalAmount: Money,
    val status: QuoteStatus,
    val items: List<QuoteItem> = emptyList()
)
