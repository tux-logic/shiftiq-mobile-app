package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.CustomerProfile
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetCustomerRegistrationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CustomerDirectoryEntry(
    val customerId: String,
    val name: String,
    val phone: String,
    val document: String
)

data class CustomerDirectoryUiState(
    val isLoading: Boolean = true,
    val branchId: String = "",
    val customers: List<CustomerDirectoryEntry> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class CustomerDirectoryViewModel @Inject constructor(
    private val getCustomerRegistrations: GetCustomerRegistrationsUseCase,
    private val getCustomerById: GetCustomerByIdUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerDirectoryUiState())
    val uiState: StateFlow<CustomerDirectoryUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val branch = sessionDataStore.activeBranchId.firstOrNull()
            if (branch == null) {
                _uiState.value = CustomerDirectoryUiState(
                    isLoading = false,
                    errorMessage = "Selecciona una sede activa para ver sus clientes."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, branchId = branch.toString(), errorMessage = null)
            when (val result = getCustomerRegistrations(branchId = branch.value, status = "ACTIVE")) {
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.error.message
                )
                is AppResult.Success -> {
                    val customers = result.data.map { registration ->
                        async {
                            val profile = (getCustomerById(registration.customerId.toString()) as? AppResult.Success)?.data
                            profile.toDirectoryEntry(registration.customerId.toString())
                        }
                    }.awaitAll().sortedBy { it.name.lowercase() }
                    _uiState.value = CustomerDirectoryUiState(
                        isLoading = false,
                        branchId = branch.toString(),
                        customers = customers
                    )
                }
            }
        }
    }

    private fun CustomerProfile?.toDirectoryEntry(fallbackId: String) = CustomerDirectoryEntry(
        customerId = fallbackId,
        name = this?.displayName?.takeIf { it.isNotBlank() } ?: "Cliente ${fallbackId.take(8)}",
        phone = this?.phone.orEmpty(),
        document = listOfNotNull(this?.documentType, this?.documentNumber).joinToString(" ")
    )
}
