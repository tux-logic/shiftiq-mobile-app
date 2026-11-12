package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation

import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem

data class InventoryBranchUi(
    val id: String,
    val name: String
)

data class InventoryUiState(
    val isLoading: Boolean = false,
    val branches: List<InventoryBranchUi> = listOf(
        InventoryBranchUi("b1000000-0000-0000-0000-000000000001", "Sede Central - ShiftIQ"),
        InventoryBranchUi("b1000000-0000-0000-0000-000000000002", "Sede Norte - Taller Express"),
        InventoryBranchUi("b1000000-0000-0000-0000-000000000003", "Sede Sur - Mantenimiento Pesado")
    ),
    val selectedBranchId: String? = "b1000000-0000-0000-0000-000000000001",
    val products: List<ProductItem> = emptyList(),
    val searchQuery: String = "",
    val categoryFilter: String = "",
    val lowStockOnly: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
    val availableCategories: List<String>
        get() = (InventorySuggestedCategoriesLocal + products.map { it.category })
            .distinct()
            .sorted()

    val criticalCount: Int
        get() = products.count { it.lowStockAlert }

    val warehouseValue: Money
        get() = products.fold(Money.ZERO) { acc, item ->
            acc + (item.salePrice * item.currentStock)
        }
}

private val InventorySuggestedCategoriesLocal =
    listOf("Filtros", "Frenos", "Aceites", "Suspensión", "Lubricantes", "Eléctrico")
