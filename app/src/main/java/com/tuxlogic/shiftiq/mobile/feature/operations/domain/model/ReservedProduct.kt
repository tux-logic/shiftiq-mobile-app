package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

data class ReservedProduct(
    val productId: ProductId,
    val productName: String,
    val quantity: Int,
    val unitPrice: Money,
    val totalPrice: Money
)
