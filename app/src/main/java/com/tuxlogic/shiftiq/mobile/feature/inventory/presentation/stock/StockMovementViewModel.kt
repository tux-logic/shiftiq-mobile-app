package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.stock

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.AddStockBatchUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.isNegativeStockError
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.mapInventoryError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockMovementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addStockBatchUseCase: AddStockBatchUseCase
) : ViewModel() {

    private val productId: ProductId? =
        savedStateHandle.get<String>(ARG_PRODUCT_ID)?.let { ProductId(it) }

    private val _uiState = MutableStateFlow(StockMovementUiState())
    val uiState: StateFlow<StockMovementUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        val id = productId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getProductByIdUseCase(id)
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            productName = detail.name,
                            currentStock = detail.currentStock
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

    fun incrementQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 1, errorMessage = null) }
    }

    fun decrementQuantity() {
        _uiState.update {
            val next = (it.quantity - 1).coerceAtLeast(1)
            it.copy(quantity = next, errorMessage = null)
        }
    }

    fun onQuantityChanged(value: Int) {
        _uiState.update { it.copy(quantity = value.coerceAtLeast(1), errorMessage = null) }
    }

    fun onAcquisitionCostChanged(value: String) {
        _uiState.update { it.copy(acquisitionCostText = value, errorMessage = null) }
    }

    fun addStock() {
        val id = productId ?: return
        val state = _uiState.value
        val cost = state.acquisitionCostText.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            addStockBatchUseCase(id, state.quantity, cost)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, navigateBack = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = mapInventoryError(error))
                    }
                }
        }
    }

    fun adjustStock() {
        val id = productId ?: return
        val state = _uiState.value
        if (state.adjustProjectedStock < 0) {
            _uiState.update { it.copy(showNegativeStockDialog = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, showNegativeStockDialog = false) }
            addStockBatchUseCase(id, -state.quantity, 0.0)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, navigateBack = true) }
                }
                .onFailure { error ->
                    val message = mapInventoryError(error)
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = message,
                            showNegativeStockDialog = isNegativeStockError(message)
                        )
                    }
                }
        }
    }

    fun dismissNegativeStockDialog() {
        _uiState.update { it.copy(showNegativeStockDialog = false) }
    }

    fun onNavigatedBack() {
        _uiState.update { it.copy(navigateBack = false) }
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
    }
}
