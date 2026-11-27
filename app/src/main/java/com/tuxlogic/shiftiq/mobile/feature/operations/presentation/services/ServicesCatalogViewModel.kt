package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.CreateServiceUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.DeleteServiceUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetServicesUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.UpdateServiceUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServicesCatalogUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val branchId: BranchId? = null,
    val services: List<ServiceItem> = emptyList(),
    val editingService: ServiceItem? = null,
    val showForm: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val serviceToDelete: ServiceItem? = null,
    val name: String = "",
    val price: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ServicesCatalogViewModel @Inject constructor(
    private val getServices: GetServicesUseCase,
    private val createService: CreateServiceUseCase,
    private val updateService: UpdateServiceUseCase,
    private val deleteService: DeleteServiceUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(ServicesCatalogUiState())
    val uiState: StateFlow<ServicesCatalogUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val branchId = sessionDataStore.activeBranchId.first()
            if (branchId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Selecciona una sede activa para ver sus servicios.") }
                return@launch
            }
            when (val result = getServices(branchId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, branchId = branchId, services = result.data)
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, branchId = branchId, errorMessage = result.error.message)
                }
            }
        }
    }

    fun openCreateForm() = _uiState.update {
        it.copy(showForm = true, editingService = null, name = "", price = "", errorMessage = null)
    }

    fun openEditForm(service: ServiceItem) = _uiState.update {
        it.copy(showForm = true, editingService = service, name = service.name, price = service.price.amount.toPlainString(), errorMessage = null)
    }

    fun closeForm() = _uiState.update { it.copy(showForm = false, errorMessage = null) }
    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updatePrice(value: String) = _uiState.update { it.copy(price = value.filter { char -> char.isDigit() || char == '.' || char == ',' }) }

    fun save() {
        val state = _uiState.value
        val branchId = state.branchId
        val price = state.price.trim().replace(',', '.').toDoubleOrNull()
        when {
            branchId == null -> { _uiState.update { it.copy(errorMessage = "No hay una sede activa.") }; return }
            state.name.isBlank() -> { _uiState.update { it.copy(errorMessage = "Ingresa el nombre del servicio.") }; return }
            price == null || price <= 0.0 -> { _uiState.update { it.copy(errorMessage = "Ingresa un precio mayor a 0.") }; return }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val editing = state.editingService
            val result = if (editing == null) createService(branchId, state.name, price)
                else updateService(editing.id, state.name, price)
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, showForm = false, successMessage = if (editing == null) "Servicio registrado en la sede." else "Servicio actualizado.") }
                    refresh()
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
            }
        }
    }

    fun requestDelete(service: ServiceItem) = _uiState.update {
        it.copy(serviceToDelete = service, showDeleteConfirmation = true, errorMessage = null)
    }

    fun dismissDelete() = _uiState.update { it.copy(serviceToDelete = null, showDeleteConfirmation = false) }

    fun confirmDelete() {
        val service = _uiState.value.serviceToDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = deleteService(ServiceId(service.id.value))) {
                is AppResult.Success -> {
                    _uiState.update { state -> state.copy(isSaving = false, showDeleteConfirmation = false, serviceToDelete = null, successMessage = "Servicio eliminado.", services = state.services.filterNot { it.id == service.id }) }
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }
}
