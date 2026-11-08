package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

enum class QuoteItemType {
    SERVICE,
    PRODUCT
}

data class QuoteItem(
    val type: QuoteItemType,
    val name: String,
    val quantity: Int,
    val unitPrice: Money,
    val total: Money
)
