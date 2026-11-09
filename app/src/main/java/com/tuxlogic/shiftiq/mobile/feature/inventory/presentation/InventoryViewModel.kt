package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.event.ActiveBranchChangedEvent
import com.tuxlogic.shiftiq.mobile.core.common.event.DomainEventBus
import com.tuxlogic.shiftiq.mobile.core.common.event.subscribe
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.AddStockBatchUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.CreateProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.DeleteProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductsUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.UpdateProductUseCase
import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val addStockBatchUseCase: AddStockBatchUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val sessionDataStore: SessionDataStore,
    private val eventBus: DomainEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Observe session active branch
        viewModelScope.launch {
            sessionDataStore.sessionState.collect { session ->
                val branchIdStr = session.activeBranchId?.toString()
                if (branchIdStr != null && branchIdStr != _uiState.value.selectedBranchId) {
                    _uiState.update { it.copy(selectedBranchId = branchIdStr) }
                    loadProducts()
                }
            }
        }

        // Subscribe to Domain Event for cross-context active branch synchronization
        viewModelScope.launch {
            eventBus.subscribe<ActiveBranchChangedEvent>().collect { event ->
                val newBranchStr = event.newBranchId.toString()
                _uiState.update { it.copy(selectedBranchId = newBranchStr, selectedProduct = null) }
                loadProducts()
            }
        }

        loadProducts()
    }

    fun selectBranch(branchId: String) {
        _uiState.update { it.copy(selectedBranchId = branchId, selectedProduct = null) }
        loadProducts()
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            loadProducts()
        }
    }

    fun setLowStockOnly(lowStockOnly: Boolean) {
        _uiState.update { it.copy(lowStockOnly = lowStockOnly) }
        loadProducts()
    }

    fun loadProducts() {
        val branchIdStr = _uiState.value.selectedBranchId ?: return
        val currentQuery = _uiState.value.searchQuery
        val currentLowStock = _uiState.value.lowStockOnly

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getProductsUseCase(
                branchId = BranchId(branchIdStr),
                name = currentQuery,
                lowStockOnly = if (currentLowStock) true else null
            )
            result.onSuccess { items ->
                _uiState.update { it.copy(isLoading = false, products = items) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProductLoading = true, errorMessage = null) }
            val result = getProductByIdUseCase(ProductId(productId))
            result.onSuccess { detail ->
                _uiState.update { it.copy(isProductLoading = false, selectedProduct = detail) }
            }.onFailure { error ->
                _uiState.update { it.copy(isProductLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun clearSelectedProduct() {
        _uiState.update { it.copy(selectedProduct = null) }
    }

    fun saveProduct(
        id: String?,
        name: String,
        category: String,
        sku: String,
        description: String?,
        price: Double,
        minimumStock: Int
    ) {
        val branchIdStr = _uiState.value.selectedBranchId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            val result = if (id == null) {
                createProductUseCase(
                    branchId = BranchId(branchIdStr),
                    category = category,
                    name = name,
                    sku = sku,
                    description = description,
                    salePrice = price,
                    minimumStock = minimumStock
                )
            } else {
                updateProductUseCase(
                    id = ProductId(id),
                    category = category,
                    name = name,
                    sku = sku,
                    description = description,
                    salePrice = price,
                    minimumStock = minimumStock
                )
            }

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        infoMessage = if (id == null) "Producto creado exitosamente." else "Producto actualizado exitosamente."
                    )
                }
                loadProducts()
                if (id != null) loadProduct(id)
            }.onFailure { error ->
                _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    fun addStock(productId: String, quantity: Int, acquisitionCost: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            val result = addStockBatchUseCase(
                productId = ProductId(productId),
                quantity = quantity,
                acquisitionCost = acquisitionCost
            )
            result.onSuccess {
                _uiState.update { it.copy(isSaving = false, infoMessage = "Movimiento de stock registrado.") }
                loadProducts()
                loadProduct(productId)
            }.onFailure { error ->
                _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = deleteProductUseCase(ProductId(productId))
            result.onSuccess {
                _uiState.update { it.copy(isSaving = false, selectedProduct = null, infoMessage = "Producto eliminado.") }
                loadProducts()
            }.onFailure { error ->
                _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }
}
