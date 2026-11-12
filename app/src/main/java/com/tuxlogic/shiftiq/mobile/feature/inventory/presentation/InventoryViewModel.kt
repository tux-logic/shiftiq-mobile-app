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
import com.tuxlogic.shiftiq.mobile.feature.inventory.application.usecase.GetProductsUseCase
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
    private val sessionDataStore: SessionDataStore,
    private val eventBus: DomainEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            sessionDataStore.sessionState.collect { session ->
                val branchIdStr = session.activeBranchId?.toString()
                if (branchIdStr != null && branchIdStr != _uiState.value.selectedBranchId) {
                    _uiState.update { it.copy(selectedBranchId = branchIdStr) }
                    loadProducts()
                }
            }
        }

        viewModelScope.launch {
            eventBus.subscribe<ActiveBranchChangedEvent>().collect { event ->
                _uiState.update {
                    it.copy(selectedBranchId = event.newBranchId.toString(), categoryFilter = "")
                }
                loadProducts()
            }
        }

        loadProducts()
    }

    fun selectBranch(branchId: String) {
        _uiState.update { it.copy(selectedBranchId = branchId, categoryFilter = "") }
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

    fun setCategoryFilter(category: String) {
        val next = if (_uiState.value.categoryFilter.equals(category, ignoreCase = true)) "" else category
        _uiState.update { it.copy(categoryFilter = next) }
        loadProducts()
    }

    fun clearCategoryFilter() {
        _uiState.update { it.copy(categoryFilter = "") }
        loadProducts()
    }

    fun setLowStockOnly(lowStockOnly: Boolean) {
        _uiState.update { it.copy(lowStockOnly = lowStockOnly) }
        loadProducts()
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    fun loadProducts() {
        val branchIdStr = _uiState.value.selectedBranchId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getProductsUseCase(
                branchId = BranchId(branchIdStr),
                name = _uiState.value.searchQuery,
                category = _uiState.value.categoryFilter.takeIf { it.isNotBlank() },
                lowStockOnly = if (_uiState.value.lowStockOnly) true else null
            )
            result.onSuccess { items ->
                _uiState.update { it.copy(isLoading = false, products = items) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = mapInventoryError(error))
                }
            }
        }
    }
}
