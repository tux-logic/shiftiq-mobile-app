package com.tuxlogic.shiftiq.mobile.feature.inventory.data.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.inventory.data.remote.dto.ProductBatchDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.data.remote.dto.ProductDetailResourceDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.data.remote.dto.ProductResourceDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.BatchId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductBatch
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.Sku

fun ProductBatchDto.toDomain(): ProductBatch = ProductBatch(
    batchId = BatchId(batchId),
    initialQuantity = initialQuantity,
    availableQuantity = availableQuantity,
    acquisitionCost = Money(acquisitionCost),
    createdAt = createdAt ?: ""
)

fun ProductResourceDto.toDomain(): ProductItem {
    val stock = currentStock ?: 0
    return ProductItem(
        id = ProductId(id),
        branchId = BranchId(branchId),
        category = category,
        name = name,
        sku = Sku(sku),
        description = description,
        salePrice = Money(salePrice),
        minimumStock = minimumStock,
        currentStock = stock,
        lowStockAlert = lowStockAlert ?: (stock <= minimumStock)
    )
}

fun ProductDetailResourceDto.toDomain(): ProductDetail {
    val stock = currentStock ?: 0
    return ProductDetail(
        id = ProductId(id),
        branchId = BranchId(branchId),
        category = category,
        name = name,
        sku = Sku(sku),
        description = description,
        salePrice = Money(salePrice),
        minimumStock = minimumStock,
        currentStock = stock,
        lowStockAlert = lowStockAlert ?: (stock <= minimumStock),
        batches = batches?.map { it.toDomain() } ?: emptyList()
    )
}
