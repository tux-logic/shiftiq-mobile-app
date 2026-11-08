package com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money

data class ProductDetail(
    val id: ProductId,
    val branchId: BranchId,
    val category: String,
    val name: String,
    val sku: Sku,
    val description: String? = null,
    val salePrice: Money,
    val minimumStock: Int,
    val currentStock: Int,
    val lowStockAlert: Boolean = currentStock <= minimumStock,
    val batches: List<ProductBatch> = emptyList()
)
