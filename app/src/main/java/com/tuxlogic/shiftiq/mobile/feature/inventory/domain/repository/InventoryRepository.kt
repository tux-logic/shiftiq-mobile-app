package com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.Sku

interface InventoryRepository {

    suspend fun getProducts(
        branchId: BranchId? = null,
        name: String? = null,
        category: String? = null,
        lowStockOnly: Boolean? = null
    ): AppResult<List<ProductItem>>

    suspend fun getProductById(id: ProductId): AppResult<ProductDetail>

    suspend fun createProduct(
        branchId: BranchId,
        category: String,
        name: String,
        sku: Sku,
        description: String?,
        salePrice: Double,
        minimumStock: Int
    ): AppResult<ProductItem>

    suspend fun updateProduct(
        id: ProductId,
        category: String,
        name: String,
        sku: Sku,
        description: String?,
        salePrice: Double,
        minimumStock: Int
    ): AppResult<ProductItem>

    suspend fun addStockBatch(
        id: ProductId,
        quantity: Int,
        acquisitionCost: Double
    ): AppResult<Int>

    suspend fun deleteProduct(id: ProductId): AppResult<Unit>
}
