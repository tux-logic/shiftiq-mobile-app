package com.tuxlogic.shiftiq.mobile.feature.core.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetUnifiedProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateEmployeeProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.UpdateOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.UnifiedProfile
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetEmployeeRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.shared.application.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val name: String = "",
    val email: String = "",
    val userId: String = "",
    val role: String = "",
    val document: String = "",
    val phone: String = "",
    val activeBranchName: String = "-",
    val specialtyName: String = "-",
    val salary: Double = 0.0,
    val isOwnerAdmin: Boolean = false,
    val isEmployee: Boolean = false,
    val profileImageUrl: String? = null,
    val profile: UnifiedProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUnifiedProfile: GetUnifiedProfileUseCase,
    private val getBranchById: GetBranchByIdUseCase,
    private val getEmployeeRegistrations: GetEmployeeRegistrationsUseCase,
    private val updateOwnerProfile: UpdateOwnerProfileUseCase,
    private val updateEmployeeProfile: UpdateEmployeeProfileUseCase,
    private val uploadImage: UploadImageUseCase,
    private val coreRepository: CoreRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val previous = _uiState.value
            _uiState.value = previous.copy(isLoading = true, errorMessage = null)
            val session = sessionDataStore.sessionState.first()
            when (val result = getUnifiedProfile()) {
                is AppResult.Failure -> _uiState.value = previous.copy(
                    isLoading = false,
                    errorMessage = result.error.message
                )
                is AppResult.Success -> {
                    val profile = result.data
                    val isOwner = session.userRole in setOf(Role.ROLE_OWNER, Role.ROLE_ADMIN)
                    val isEmployee = session.userRole == Role.ROLE_EMPLOYEE
                    val personName = profile.employee?.fullName
                        ?.takeIf(String::isNotBlank)
                        ?: profile.owner?.fullName?.takeIf(String::isNotBlank)
                        ?: profile.customer?.displayName.orEmpty()
                    val personDocument = profile.employee?.let { "${it.documentType}: ${it.documentNumber}" }
                        ?: profile.owner?.let { "${it.documentType}: ${it.documentNumber}" }
                        ?: profile.customer?.let { "${it.documentType}: ${it.documentNumber}" }
                        ?: "No registrado"
                    val personPhone = profile.employee?.phone
                        ?: profile.owner?.phone
                        ?: profile.customer?.phone
                        ?: "No registrado"
                    val roleName = when (session.userRole) {
                        Role.ROLE_OWNER -> "Propietario / Gerente General (Owner)"
                        Role.ROLE_ADMIN -> "Administrador de Plataforma"
                        Role.ROLE_EMPLOYEE -> "Mecánico Especialista (B2B Staff)"
                        else -> profile.roles.joinToString(" · ").ifBlank { "Personal del Taller" }
                    }
                    var branchName = "-"
                    val activeBranchId = session.activeBranchId?.value?.toString().orEmpty()
                    if (activeBranchId.isNotBlank()) {
                        branchName = when (val branch = getBranchById(activeBranchId)) {
                            is AppResult.Success -> branch.data.name
                            is AppResult.Failure -> "-"
                        }
                    }
                    var specialty = "-"
                    var salary = 0.0
                    profile.employee?.employeeId?.let { employeeId ->
                        runCatching { UUID.fromString(employeeId) }.getOrNull()?.let { id ->
                            val registrations = getEmployeeRegistrations(employeeId = id)
                            if (registrations is AppResult.Success) {
                                registrations.data.firstOrNull { it.isActive }?.let { registration ->
                                    specialty = registration.specialityName
                                        ?.takeIf(String::isNotBlank)
                                        ?: registration.speciality
                                    salary = registration.salary
                                }
                            }
                        }
                    }
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        name = personName,
                        email = session.userEmail.orEmpty(),
                        userId = session.userId?.value?.toString().orEmpty(),
                        role = roleName,
                        document = personDocument,
                        phone = personPhone,
                        activeBranchName = branchName,
                        specialtyName = specialty,
                        salary = salary,
                        isOwnerAdmin = isOwner,
                        isEmployee = isEmployee,
                        profileImageUrl = profile.profileImageUrl,
                        profile = profile
                    )
                }
            }
        }
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        phone: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val profile = _uiState.value.profile ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)
            val result = when {
                profile.employee != null -> updateEmployeeProfile(
                    employeeId = profile.employee.employeeId,
                    firstName = firstName,
                    lastName = lastName,
                    documentType = profile.employee.documentType,
                    documentNumber = profile.employee.documentNumber,
                    phone = phone
                )
                profile.owner != null -> updateOwnerProfile(
                    ownerId = profile.owner.id,
                    firstName = firstName,
                    lastName = lastName,
                    documentType = profile.owner.documentType,
                    documentNumber = profile.owner.documentNumber,
                    phone = phone
                )
                else -> null
            }
            when (result) {
                is AppResult.Success -> {
                    refresh()
                    _uiState.value = _uiState.value.copy(isSaving = false, successMessage = "Perfil actualizado.")
                    onComplete(true)
                }
                is AppResult.Failure -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = result.error.message)
                    onComplete(false)
                }
                null -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = "No se encontró un perfil para esta cuenta.")
                    onComplete(false)
                }
            }
        }
    }

    fun uploadProfileImage(bytes: ByteArray, fileName: String, mimeType: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingImage = true, errorMessage = null)
            when (val upload = uploadImage(bytes, fileName, mimeType, "avatars")) {
                is AppResult.Failure -> _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    errorMessage = upload.error.message
                )
                is AppResult.Success -> when (val update = coreRepository.updateProfileImage(upload.data.url)) {
                    is AppResult.Failure -> _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = update.error.message
                    )
                    is AppResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            profileImageUrl = update.data.profileImageUrl,
                            profile = update.data,
                            successMessage = "Foto de perfil actualizada."
                        )
                    }
                }
            }
        }
    }
}
