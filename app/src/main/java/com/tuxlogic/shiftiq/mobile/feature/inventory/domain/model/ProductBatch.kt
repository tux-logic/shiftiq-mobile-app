package com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

data class ProductBatch(
    val batchId: BatchId,
    val initialQuantity: Int,
    val availableQuantity: Int,
    val acquisitionCost: Money,
    val createdAt: String
)
