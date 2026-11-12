package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.form

data class ProductFormUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val branchId: String? = null,
    val sku: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val salePriceText: String = "",
    val minimumStockText: String = "",
    val errorMessage: String? = null,
    val showDuplicateSkuDialog: Boolean = false,
    val navigateBack: Boolean = false
)
