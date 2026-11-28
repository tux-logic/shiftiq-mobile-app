package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation

import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem

enum class InventoryStockFilter {
    ALL,
    LOW_STOCK,
    OUT_OF_STOCK
}

data class InventoryUiState(
    val isLoading: Boolean = false,
    val selectedBranchId: String? = null,
    val activeBranchName: String = "Sede Principal",
    val products: List<ProductItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "TODOS",
    val stockFilter: InventoryStockFilter = InventoryStockFilter.ALL,
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
    val visibleProducts: List<ProductItem>
        get() = when (stockFilter) {
            InventoryStockFilter.ALL -> products
            InventoryStockFilter.LOW_STOCK -> products.filter { it.lowStockAlert }
            InventoryStockFilter.OUT_OF_STOCK -> products.filter { it.currentStock <= 0 }
        }

    val criticalCount: Int
        get() = products.count { it.lowStockAlert }

    val outOfStockCount: Int
        get() = products.count { it.currentStock <= 0 }

    val warehouseValue: Money
        get() = products.fold(Money.ZERO) { acc, item ->
            acc + (item.salePrice * item.currentStock)
        }
}
