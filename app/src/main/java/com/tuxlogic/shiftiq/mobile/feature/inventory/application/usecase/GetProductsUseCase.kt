package com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        branchId: BranchId? = null,
        name: String? = null,
        category: String? = null,
        lowStockOnly: Boolean? = null
    ): AppResult<List<ProductItem>> {
        val trimmedName = name?.trim()?.takeIf { it.isNotBlank() }
        val trimmedCategory = category?.trim()?.takeIf { it.isNotBlank() }
        return inventoryRepository.getProducts(
            branchId = branchId,
            name = trimmedName,
            category = trimmedCategory,
            lowStockOnly = lowStockOnly
        )
    }
}
