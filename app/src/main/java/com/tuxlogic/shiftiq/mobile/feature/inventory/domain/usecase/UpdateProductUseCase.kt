package com.tuxlogic.shiftiq.mobile.feature.inventory.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.Sku
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        id: ProductId,
        category: String,
        name: String,
        sku: String,
        description: String? = null,
        salePrice: Double,
        minimumStock: Int
    ): AppResult<ProductItem> {
        val trimmedCategory = category.trim()
        if (trimmedCategory.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "category", message = "Product category cannot be blank")
            )
        }
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "name", message = "Product name cannot be blank")
            )
        }
        val trimmedSku = sku.trim()
        if (trimmedSku.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "sku", message = "Product SKU cannot be blank")
            )
        }
        if (trimmedSku.length > 50) {
            return AppResult.Failure(
                AppError.Validation(details = "sku", message = "Product SKU cannot exceed 50 characters")
            )
        }
        if (salePrice <= 0.0) {
            return AppResult.Failure(
                AppError.Validation(details = "salePrice", message = "Sale price must be greater than zero")
            )
        }
        if (minimumStock < 0) {
            return AppResult.Failure(
                AppError.Validation(details = "minimumStock", message = "Minimum stock cannot be negative")
            )
        }

        return inventoryRepository.updateProduct(
            id = id,
            category = trimmedCategory,
            name = trimmedName,
            sku = Sku(trimmedSku),
            description = description?.trim()?.takeIf { it.isNotBlank() },
            salePrice = salePrice,
            minimumStock = minimumStock
        )
    }
}
