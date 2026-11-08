package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

enum class PaymentMethod {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER
}

data class Payment(
    val id: PaymentId,
    val amount: Money,
    val method: PaymentMethod,
    val paidAt: String? = null
)
