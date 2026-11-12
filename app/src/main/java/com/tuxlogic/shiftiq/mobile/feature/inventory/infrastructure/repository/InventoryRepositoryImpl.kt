package com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.api.InventoryApiService
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.AddBatchRequestDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.CreateProductRequestDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.infrastructure.remote.dto.UpdateProductRequestDto
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.Sku
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryApiService: InventoryApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : InventoryRepository {

    override suspend fun getProducts(
        branchId: BranchId?,
        name: String?,
        category: String?,
        lowStockOnly: Boolean?
    ): AppResult<List<ProductItem>> = withContext(dispatchers.io) {
        val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
        if (effectiveBranchId == null) {
            return@withContext AppResult.Failure(
                AppError.Validation(details = "branchId", message = "No active branch selected")
            )
        }
        safeApiCall {
            inventoryApiService.getProducts(
                branchId = effectiveBranchId.toString(),
                name = name,
                category = category,
                lowStockOnly = lowStockOnly
            )
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: ProductId): AppResult<ProductDetail> =
        withContext(dispatchers.io) {
            safeApiCall {
                inventoryApiService.getProductById(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun createProduct(
        branchId: BranchId,
        category: String,
        name: String,
        sku: Sku,
        description: String?,
        salePrice: Double,
        minimumStock: Int
    ): AppResult<ProductItem> = withContext(dispatchers.io) {
        safeApiCall {
            inventoryApiService.createProduct(
                CreateProductRequestDto(
                    branchId = branchId.toString(),
                    category = category,
                    name = name,
                    sku = sku.toString(),
                    description = description,
                    salePrice = salePrice,
                    minimumStock = minimumStock
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun updateProduct(
        id: ProductId,
        category: String,
        name: String,
        sku: Sku,
        description: String?,
        salePrice: Double,
        minimumStock: Int
    ): AppResult<ProductItem> = withContext(dispatchers.io) {
        safeApiCall {
            inventoryApiService.updateProduct(
                productId = id.toString(),
                request = UpdateProductRequestDto(
                    name = name,
                    category = category,
                    sku = sku.toString(),
                    description = description,
                    salePrice = salePrice,
                    minimumStock = minimumStock
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun addStockBatch(
        id: ProductId,
        quantity: Int,
        acquisitionCost: Double
    ): AppResult<Int> = withContext(dispatchers.io) {
        safeApiCall {
            inventoryApiService.addStockBatch(
                productId = id.toString(),
                request = AddBatchRequestDto(
                    quantity = quantity,
                    acquisitionCost = acquisitionCost
                )
            )
        }.map { it.availableQuantity }
    }

    override suspend fun deleteProduct(id: ProductId): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                inventoryApiService.deleteProduct(id.toString())
            }
        }
}
