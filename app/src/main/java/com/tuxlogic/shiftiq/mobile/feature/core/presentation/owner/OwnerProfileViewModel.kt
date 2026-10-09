package com.tuxlogic.shiftiq.mobile.feature.core.presentation.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OwnerProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.CreateOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.GetOwnerProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OwnerProfileUiState(
    val userId: String = "",
    val profile: OwnerProfile? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val documentType: String = "DNI",
    val documentNumber: String = "",
    val documentNumberError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase,
    private val createOwnerProfileUseCase: CreateOwnerProfileUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerProfileUiState())
    val uiState: StateFlow<OwnerProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val session = sessionDataStore.sessionState.first()
            val userId = session.userId?.toString()

            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sesión no disponible") }
                return@launch
            }

            _uiState.update { it.copy(userId = userId) }

            val result = getOwnerProfileUseCase(userId)
            result.onSuccess { owner ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = owner,
                        firstName = owner.firstName,
                        lastName = owner.lastName,
                        documentType = owner.documentType,
                        documentNumber = owner.documentNumber,
                        phone = owner.phone
                    )
                }
            }.onFailure {
                // Perfil no creado aún
                _uiState.update { it.copy(isLoading = false, profile = null) }
            }
        }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { it.copy(firstName = value, firstNameError = null, errorMessage = null) }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { it.copy(lastName = value, lastNameError = null, errorMessage = null) }
    }

    fun onDocumentTypeChanged(value: String) {
        _uiState.update { it.copy(documentType = value) }
    }

    fun onDocumentNumberChanged(value: String) {
        _uiState.update { it.copy(documentNumber = value, documentNumberError = null, errorMessage = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null, errorMessage = null) }
    }

    fun saveProfile() {
        val current = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = createOwnerProfileUseCase(
                userId = current.userId,
                firstName = current.firstName,
                lastName = current.lastName,
                documentType = current.documentType,
                documentNumber = current.documentNumber,
                phone = current.phone
            )

            result.onSuccess { owner ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = owner,
                        isSuccess = true
                    )
                }
            }.onFailure { error ->
                when (error) {
                    is AppError.Validation -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                firstNameError = if (error.details == "firstName") error.message else null,
                                lastNameError = if (error.details == "lastName") error.message else null,
                                documentNumberError = if (error.details == "documentNumber") error.message else null,
                                phoneError = if (error.details == "phone") error.message else null,
                                errorMessage = if (error.details !in listOf("firstName", "lastName", "documentNumber", "phone")) error.message else null
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
