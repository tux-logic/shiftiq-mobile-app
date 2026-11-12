package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.stock

data class StockMovementUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val productName: String = "",
    val currentStock: Int = 0,
    val quantity: Int = 1,
    val acquisitionCostText: String = "",
    val errorMessage: String? = null,
    val showNegativeStockDialog: Boolean = false,
    val navigateBack: Boolean = false
) {
    val projectedStock: Int get() = currentStock + quantity

    val adjustProjectedStock: Int get() = currentStock - quantity
}
