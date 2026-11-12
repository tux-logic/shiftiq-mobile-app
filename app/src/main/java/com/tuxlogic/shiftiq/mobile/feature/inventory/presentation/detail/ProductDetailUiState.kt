package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.detail

import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductDetail

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val detail: ProductDetail? = null,
    val errorMessage: String? = null,
    val showInUseDialog: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val navigateBack: Boolean = false
)
