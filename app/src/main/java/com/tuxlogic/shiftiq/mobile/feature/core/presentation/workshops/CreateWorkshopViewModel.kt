package com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.CreateWorkshopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateWorkshopUiState(
    val ownerId: String = "",
    val businessName: String = "",
    val businessNameError: String? = null,
    val brandName: String = "",
    val brandNameError: String? = null,
    val taxId: String = "",
    val taxIdError: String? = null,
    val mileageInterval: String = "5000",
    val mileageIntervalError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CreateWorkshopViewModel @Inject constructor(
    private val createWorkshopUseCase: CreateWorkshopUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateWorkshopUiState())
    val uiState: StateFlow<CreateWorkshopUiState> = _uiState.asStateFlow()

    fun setOwnerId(ownerId: String) {
        _uiState.update { it.copy(ownerId = ownerId) }
    }

    fun onBusinessNameChanged(value: String) {
        _uiState.update { it.copy(businessName = value, businessNameError = null, errorMessage = null) }
    }

    fun onBrandNameChanged(value: String) {
        _uiState.update { it.copy(brandName = value, brandNameError = null, errorMessage = null) }
    }

    fun onTaxIdChanged(value: String) {
        if (value.length <= 11 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(taxId = value, taxIdError = null, errorMessage = null) }
        }
    }

    fun onMileageIntervalChanged(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(mileageInterval = value, mileageIntervalError = null, errorMessage = null) }
        }
    }

    fun createWorkshop() {
        val current = _uiState.value
        val interval = current.mileageInterval.toIntOrNull() ?: 5000

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = createWorkshopUseCase(
                ownerId = current.ownerId,
                businessName = current.businessName,
                brandName = current.brandName,
                taxId = current.taxId,
                mileageIntervalConfig = interval
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                when (error) {
                    is AppError.Validation -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                businessNameError = if (error.details == "businessName") error.message else null,
                                brandNameError = if (error.details == "brandName") error.message else null,
                                taxIdError = if (error.details == "taxId") error.message else null,
                                mileageIntervalError = if (error.details == "mileageInterval") error.message else null,
                                errorMessage = if (error.details !in listOf("businessName", "brandName", "taxId", "mileageInterval")) error.message else null
                            )
                        }
                    }
                    is AppError.Conflict -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "El RUC ya está registrado para otro taller o ya tienes un taller registrado."
                            )
                        }
                    }
                    else -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
                }
            }
        }
    }
}
