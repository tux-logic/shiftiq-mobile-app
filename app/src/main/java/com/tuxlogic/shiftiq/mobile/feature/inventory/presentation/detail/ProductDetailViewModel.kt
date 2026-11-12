package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.DeleteProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.isProductInUseError
import com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.mapInventoryError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val productId: ProductId? =
        savedStateHandle.get<String>(ARG_PRODUCT_ID)?.let { ProductId(it) }

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        val id = productId ?: run {
            _uiState.update { it.copy(errorMessage = "Producto no encontrado.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getProductByIdUseCase(id)
                .onSuccess { detail ->
                    _uiState.update { it.copy(isLoading = false, detail = detail) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = mapInventoryError(error))
                    }
                }
        }
    }

    fun requestDelete() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun dismissDeleteConfirm() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun confirmDelete() {
        val id = productId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, showDeleteConfirm = false, errorMessage = null) }
            deleteProductUseCase(id)
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, navigateBack = true) }
                }
                .onFailure { error ->
                    val message = mapInventoryError(error)
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = message,
                            showInUseDialog = isProductInUseError(message)
                        )
                    }
                }
        }
    }

    fun dismissInUseDialog() {
        _uiState.update { it.copy(showInUseDialog = false) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onNavigatedBack() {
        _uiState.update { it.copy(navigateBack = false) }
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
    }
}
