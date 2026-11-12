package com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class AddStockBatchUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        productId: ProductId,
        quantity: Int,
        acquisitionCost: Double = 0.0
    ): AppResult<Int> {
        if (quantity == 0) {
            return AppResult.Failure(
                AppError.Validation(details = "quantity", message = "Batch quantity cannot be zero")
            )
        }
        if (quantity > 0 && acquisitionCost < 0.0) {
            return AppResult.Failure(
                AppError.Validation(details = "acquisitionCost", message = "Acquisition cost cannot be negative for stock entries")
            )
        }
        val safeCost = if (quantity < 0) 0.0 else acquisitionCost
        return inventoryRepository.addStockBatch(productId, quantity, safeCost)
    }
}
