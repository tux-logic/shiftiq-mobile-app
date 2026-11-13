package com.tuxlogic.shiftiq.mobile.feature.core.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.CreateCustomerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateCustomerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.SendOtpUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.UpdateEmailUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.UpdatePasswordUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerAccountUiState(
    val isLoading: Boolean = true,
    val isSavingProfile: Boolean = false,
    val isSendingOtp: Boolean = false,
    val isUpdatingEmail: Boolean = false,
    val isUpdatingPassword: Boolean = false,
    val userId: String = "",
    val customerId: String = "",
    val canCreateProfile: Boolean = false,
    val email: String = "",
    val isCorporate: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val businessName: String = "",
    val documentType: String = "",
    val documentNumber: String = "",
    val phone: String = "",
    val newEmail: String = "",
    val emailOtp: String = "",
    val emailOtpSent: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class CustomerAccountViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val createCustomerProfile: CreateCustomerProfileUseCase,
    private val updateCustomerProfile: UpdateCustomerProfileUseCase,
    private val sendOtp: SendOtpUseCase,
    private val verifyOtp: VerifyOtpUseCase,
    private val updateEmail: UpdateEmailUseCase,
    private val updatePassword: UpdatePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerAccountUiState())
    val uiState: StateFlow<CustomerAccountUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val session = sessionDataStore.sessionState.first()
            val userId = session.userId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Your session has expired. Sign in again.") }
                return@launch
            }

            val userIdValue = userId.value.toString()
            val base = CustomerAccountUiState(
                isLoading = false,
                userId = userIdValue,
                email = session.userEmail.orEmpty()
            )
            when (val result = getCustomerByUserId(userIdValue)) {
                is AppResult.Failure -> {
                    val profileMissing = result.error is com.tuxlogic.shiftiq.mobile.core.common.result.AppError.NotFound
                    _uiState.value = base.copy(
                        canCreateProfile = profileMissing,
                        errorMessage = if (profileMissing) "No customer profile exists yet. Complete the form to create one." else result.error.message
                    )
                }
                is AppResult.Success -> {
                    val customer = result.data
                    _uiState.value = base.copy(
                        customerId = customer.id,
                        email = session.userEmail.orEmpty(),
                        isCorporate = customer.isCorporate,
                        firstName = customer.firstName.orEmpty(),
                        lastName = customer.lastName.orEmpty(),
                        businessName = customer.businessName.orEmpty(),
                        documentType = customer.documentType,
                        documentNumber = customer.documentNumber,
                        phone = customer.phone
                    )
                }
            }
        }
    }

    fun onFirstNameChange(value: String) = update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = update { it.copy(lastName = value) }
    fun onBusinessNameChange(value: String) = update { it.copy(businessName = value) }
    fun onPhoneChange(value: String) = update { it.copy(phone = value) }
    fun onDocumentNumberChange(value: String) = update { it.copy(documentNumber = value) }
    fun onCustomerTypeChange(isCorporate: Boolean) = update {
        it.copy(isCorporate = isCorporate, documentType = if (isCorporate) "RUC" else "DNI")
    }
    fun onNewEmailChange(value: String) = update { it.copy(newEmail = value, emailOtpSent = false, emailOtp = "") }
    fun onEmailOtpChange(value: String) = update { it.copy(emailOtp = value.filter(Char::isDigit).take(6)) }
    fun onCurrentPasswordChange(value: String) = update { it.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) = update { it.copy(newPassword = value) }
    fun onConfirmPasswordChange(value: String) = update { it.copy(confirmPassword = value) }

    fun saveProfile() {
        val state = _uiState.value
        if (state.firstName.isBlank() && !state.isCorporate || state.lastName.isBlank() && !state.isCorporate ||
            state.businessName.isBlank() && state.isCorporate || state.phone.isBlank() || state.documentNumber.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Complete the required customer profile fields.", successMessage = null) }
            return
        }
        if (state.customerId.isBlank() && !state.canCreateProfile) {
            _uiState.update { it.copy(errorMessage = "The profile could not be checked. Reload it before attempting to create one.", successMessage = null) }
            return
        }
        if (state.isCorporate && state.documentType.uppercase() != "RUC" || !state.isCorporate && state.documentType.uppercase() != "DNI") {
            _uiState.update { it.copy(errorMessage = "The customer document type does not match the profile type.", successMessage = null) }
            return
        }

        _uiState.update { it.copy(isSavingProfile = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val result = if (state.customerId.isBlank()) {
                createCustomerProfile(
                    userId = state.userId,
                    firstName = state.firstName.takeIf { !state.isCorporate },
                    lastName = state.lastName.takeIf { !state.isCorporate },
                    businessName = state.businessName.takeIf { state.isCorporate },
                    documentType = state.documentType,
                    documentNumber = state.documentNumber,
                    phone = state.phone
                )
            } else {
                updateCustomerProfile(
                    customerId = state.customerId,
                    firstName = state.firstName.takeIf { !state.isCorporate },
                    lastName = state.lastName.takeIf { !state.isCorporate },
                    businessName = state.businessName.takeIf { state.isCorporate },
                    documentType = state.documentType,
                    documentNumber = state.documentNumber,
                    phone = state.phone
                )
            }
            when (result) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isSavingProfile = false,
                        customerId = result.data.id,
                        canCreateProfile = false,
                        successMessage = "Customer profile saved.",
                        errorMessage = null
                    )
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSavingProfile = false, errorMessage = result.error.message) }
            }
        }
    }

    fun requestEmailCode() {
        val state = _uiState.value
        val email = state.newEmail.trim()
        if (!EMAIL_PATTERN.matches(email) || email.equals(state.email, ignoreCase = true)) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email address different from your current one.", successMessage = null) }
            return
        }
        _uiState.update { it.copy(isSendingOtp = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            when (val result = sendOtp(email)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isSendingOtp = false, emailOtpSent = true, successMessage = "A verification code was sent.")
                }
                is AppResult.Failure -> _uiState.update { it.copy(isSendingOtp = false, errorMessage = result.error.message) }
            }
        }
    }

    fun verifyAndUpdateEmail() {
        val state = _uiState.value
        val userId = runCatching { UserId(java.util.UUID.fromString(state.userId)) }.getOrNull()
        if (!state.emailOtpSent || state.emailOtp.length != 6 || userId == null) {
            _uiState.update { it.copy(errorMessage = "Request and enter the six-digit email verification code first.") }
            return
        }
        _uiState.update { it.copy(isUpdatingEmail = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            when (val verification = verifyOtp(state.newEmail.trim(), state.emailOtp)) {
                is AppResult.Failure -> _uiState.update { it.copy(isUpdatingEmail = false, errorMessage = verification.error.message) }
                is AppResult.Success -> {
                    if (!verification.data) {
                        _uiState.update { it.copy(isUpdatingEmail = false, errorMessage = "The verification code was rejected.") }
                        return@launch
                    }
                    when (val result = updateEmail(userId, state.newEmail.trim())) {
                        is AppResult.Success -> _uiState.update {
                            it.copy(
                                isUpdatingEmail = false,
                                email = state.newEmail.trim(),
                                newEmail = "",
                                emailOtp = "",
                                emailOtpSent = false,
                                successMessage = "Email address updated.",
                                errorMessage = null
                            )
                        }
                        is AppResult.Failure -> _uiState.update { it.copy(isUpdatingEmail = false, errorMessage = result.error.message) }
                    }
                }
            }
        }
    }

    fun changePassword() {
        val state = _uiState.value
        val userId = runCatching { UserId(java.util.UUID.fromString(state.userId)) }.getOrNull()
        if (userId == null || state.currentPassword.isBlank() || state.newPassword.length < 8 || state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Enter your current password and matching new passwords of at least eight characters.", successMessage = null) }
            return
        }
        _uiState.update { it.copy(isUpdatingPassword = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            when (val result = updatePassword(userId, state.currentPassword, state.newPassword)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isUpdatingPassword = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        successMessage = "Password updated.",
                        errorMessage = null
                    )
                }
                is AppResult.Failure -> _uiState.update { it.copy(isUpdatingPassword = false, errorMessage = result.error.message) }
            }
        }
    }

    private fun update(transform: (CustomerAccountUiState) -> CustomerAccountUiState) {
        _uiState.update { transform(it).copy(errorMessage = null, successMessage = null) }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
