package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.CreateProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.UpdateProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.isDuplicateSkuError
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.mapInventoryError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionDataStore: SessionDataStore,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase
) : ViewModel() {

    private val editProductId: ProductId? =
        savedStateHandle.get<String>(ARG_PRODUCT_ID)
            ?.takeIf { it.isNotBlank() && it != "none" }
            ?.let { ProductId(it) }

    private val routeBranchId: String? =
        savedStateHandle.get<String>(ARG_BRANCH_ID)
            ?.takeIf { it.isNotBlank() && it != "none" }

    private val _uiState = MutableStateFlow(
        ProductFormUiState(
            isEditMode = editProductId != null,
            branchId = routeBranchId
        )
    )
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionDataStore.sessionState.collect { session ->
                val sessionBranch = session.activeBranchId?.toString()
                _uiState.update { state ->
                    state.copy(
                        branchId = sessionBranch
                            ?: state.branchId
                            ?: routeBranchId
                            ?: DEFAULT_BRANCH_ID
                    )
                }
            }
        }
        if (editProductId != null) {
            loadForEdit(editProductId)
        }
    }

    private fun loadForEdit(productId: ProductId) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getProductByIdUseCase(productId)
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sku = detail.sku.value,
                            name = detail.name,
                            category = detail.category,
                            description = detail.description.orEmpty(),
                            salePriceText = detail.salePrice.amount.toPlainString(),
                            minimumStockText = detail.minimumStock.toString()
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = mapInventoryError(error))
                    }
                }
        }
    }

    fun onSkuChanged(value: String) {
        _uiState.update { it.copy(sku = value, errorMessage = null) }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun onCategoryChanged(value: String) {
        _uiState.update { it.copy(category = value, errorMessage = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onSalePriceChanged(value: String) {
        _uiState.update { it.copy(salePriceText = value, errorMessage = null) }
    }

    fun onMinimumStockChanged(value: String) {
        _uiState.update { it.copy(minimumStockText = value, errorMessage = null) }
    }

    fun submit() {
        val state = _uiState.value
        val price = state.salePriceText.trim().replace(",", ".").toDoubleOrNull()
        val minimum = state.minimumStockText.trim().toIntOrNull()
        if (price == null || minimum == null) {
            _uiState.update { it.copy(errorMessage = "Revisa el precio y el stock mínimo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, showDuplicateSkuDialog = false) }
            val description = state.description.trim().takeIf { it.isNotBlank() }
            val result = if (state.isEditMode && editProductId != null) {
                updateProductUseCase(
                    id = editProductId,
                    category = state.category,
                    name = state.name,
                    sku = state.sku,
                    description = description,
                    salePrice = price,
                    minimumStock = minimum
                )
            } else {
                val branchIdStr = state.branchId
                if (branchIdStr.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = "Selecciona una sede activa antes de registrar.")
                    }
                    return@launch
                }
                createProductUseCase(
                    branchId = BranchId(branchIdStr),
                    category = state.category,
                    name = state.name,
                    sku = state.sku,
                    description = description,
                    salePrice = price,
                    minimumStock = minimum
                )
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, navigateBack = true) }
                }
                .onFailure { error ->
                    val message = mapInventoryError(error)
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = message,
                            showDuplicateSkuDialog = !state.isEditMode && isDuplicateSkuError(message)
                        )
                    }
                }
        }
    }

    fun dismissDuplicateSkuDialog() {
        _uiState.update { it.copy(showDuplicateSkuDialog = false) }
    }

    fun onNavigatedBack() {
        _uiState.update { it.copy(navigateBack = false) }
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
        const val ARG_BRANCH_ID = "branchId"
        private const val DEFAULT_BRANCH_ID = "b1000000-0000-0000-0000-000000000001"
    }
}
