package com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(id: ProductId): AppResult<ProductDetail> {
        return inventoryRepository.getProductById(id)
    }
}
