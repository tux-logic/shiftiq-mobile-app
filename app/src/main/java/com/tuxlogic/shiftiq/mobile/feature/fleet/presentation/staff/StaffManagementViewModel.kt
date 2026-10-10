package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.ApproveEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.CreateEmployeeRegistrationUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.GetEmployeeRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.usecase.RejectEmployeeRegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class StaffManagementUiState(
    val isLoading: Boolean = false,
    val registrations: List<EmployeeRegistration> = emptyList(),
    val activeRegistrations: List<EmployeeRegistration> = emptyList(),
    val pendingRegistrations: List<EmployeeRegistration> = emptyList(),
    val selectedTab: Int = 0, // 0 = Personal Activo, 1 = Solicitudes Pendientes
    val activeBranchId: UUID? = null,
    val showAddDialog: Boolean = false,
    val addEmployeeId: String = "",
    val addSpeciality: String = "",
    val addSpecialityName: String = "",
    val addSalary: String = "",
    val addRole: String = "ROLE_TECHNICIAN",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class StaffManagementViewModel @Inject constructor(
    private val getEmployeeRegistrationsUseCase: GetEmployeeRegistrationsUseCase,
    private val approveEmployeeRegistrationUseCase: ApproveEmployeeRegistrationUseCase,
    private val rejectEmployeeRegistrationUseCase: RejectEmployeeRegistrationUseCase,
    private val createEmployeeRegistrationUseCase: CreateEmployeeRegistrationUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffManagementUiState())
    val uiState: StateFlow<StaffManagementUiState> = _uiState.asStateFlow()

    init {
        loadRegistrations()
    }

    fun loadRegistrations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val branchId = sessionDataStore.activeBranchId.firstOrNull()?.value
            _uiState.update { it.copy(activeBranchId = branchId) }

            getEmployeeRegistrationsUseCase(branchId = branchId)
                .onSuccess { list ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            registrations = list,
                            activeRegistrations = list.filter { it.isActive },
                            pendingRegistrations = list.filter { it.isPending }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun approveRegistration(id: UUID) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            approveEmployeeRegistrationUseCase(id)
                .onSuccess {
                    loadRegistrations()
                    _uiState.update { it.copy(successMessage = "Solicitud aprobada e integrada a la sede.") }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun rejectRegistration(id: UUID, reason: String? = "Rechazado por administración") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            rejectEmployeeRegistrationUseCase(id, reason)
                .onSuccess {
                    loadRegistrations()
                    _uiState.update { it.copy(successMessage = "Solicitud rechazada.") }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun openAddDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = true,
                addEmployeeId = "",
                addSpeciality = "Mecánica General",
                addSpecialityName = "Mecánica General",
                addSalary = "2500.0",
                addRole = "ROLE_TECHNICIAN"
            )
        }
    }

    fun closeAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onAddEmployeeIdChanged(v: String) = _uiState.update { it.copy(addEmployeeId = v) }
    fun onAddSpecialityChanged(v: String) = _uiState.update { it.copy(addSpeciality = v, addSpecialityName = v) }
    fun onAddSalaryChanged(v: String) = _uiState.update { it.copy(addSalary = v) }
    fun onAddRoleChanged(v: String) = _uiState.update { it.copy(addRole = v) }

    fun submitAddStaff() {
        val branchId = _uiState.value.activeBranchId
        if (branchId == null) {
            _uiState.update { it.copy(errorMessage = "No hay una sede activa seleccionada.") }
            return
        }

        val employeeUuid = runCatching { UUID.fromString(_uiState.value.addEmployeeId.trim()) }.getOrNull()
        if (employeeUuid == null) {
            _uiState.update { it.copy(errorMessage = "El ID de empleado debe ser un UUID válido.") }
            return
        }

        val salaryDouble = _uiState.value.addSalary.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            createEmployeeRegistrationUseCase(
                employeeId = employeeUuid,
                branchId = branchId,
                speciality = _uiState.value.addSpeciality.ifBlank { "Mecánica General" },
                specialityName = _uiState.value.addSpecialityName.ifBlank { "Mecánica General" },
                salary = salaryDouble,
                role = _uiState.value.addRole
            ).onSuccess {
                _uiState.update { it.copy(showAddDialog = false, successMessage = "Personal registrado exitosamente.") }
                loadRegistrations()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
