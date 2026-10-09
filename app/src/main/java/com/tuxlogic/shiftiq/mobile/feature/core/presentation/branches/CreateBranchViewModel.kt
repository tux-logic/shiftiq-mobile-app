package com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.CreateBranchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateBranchUiState(
    val workshopId: String = "",
    val code: String = "",
    val codeError: String? = null,
    val name: String = "",
    val nameError: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CreateBranchViewModel @Inject constructor(
    private val createBranchUseCase: CreateBranchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBranchUiState())
    val uiState: StateFlow<CreateBranchUiState> = _uiState.asStateFlow()

    fun setWorkshopId(workshopId: String) {
        _uiState.update { it.copy(workshopId = workshopId) }
    }

    fun onCodeChanged(value: String) {
        _uiState.update { it.copy(code = value.uppercase(), codeError = null, errorMessage = null) }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, errorMessage = null) }
    }

    fun onAddressChanged(value: String) {
        _uiState.update { it.copy(address = value, addressError = null, errorMessage = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null, errorMessage = null) }
    }

    fun createBranch() {
        val current = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = createBranchUseCase(
                workshopId = current.workshopId,
                code = current.code,
                name = current.name,
                address = current.address,
                phone = current.phone
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                when (error) {
                    is AppError.Validation -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                codeError = if (error.details == "code") error.message else null,
                                nameError = if (error.details == "name") error.message else null,
                                addressError = if (error.details == "address") error.message else null,
                                phoneError = if (error.details == "phone") error.message else null,
                                errorMessage = if (error.details !in listOf("code", "name", "address", "phone")) error.message else null
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
