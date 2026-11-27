package com.tuxlogic.shiftiq.mobile.feature.core.presentation.operatinghours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursDay
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursException
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursExceptionInput
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursScope
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.OperatingHoursRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OperatingHoursUiState(
    val branchId: String? = null,
    val workshopId: String? = null,
    val branchName: String = "",
    val scope: OperatingHoursScope = OperatingHoursScope.BRANCH,
    val days: List<OperatingHoursDay> = emptyList(),
    val exceptions: List<OperatingHoursException> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val noticeMessage: String? = null
) {
    val canEditWorkshop: Boolean get() = !workshopId.isNullOrBlank()
}

@HiltViewModel
class OperatingHoursViewModel @Inject constructor(
    private val repository: OperatingHoursRepository,
    private val coreRepository: CoreRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(OperatingHoursUiState())
    val uiState: StateFlow<OperatingHoursUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, noticeMessage = null) }
        val branchId = sessionDataStore.activeBranchId.first()?.toString()
        if (branchId.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Selecciona primero una sede activa.") }
            return@launch
        }

        var branch: Branch? = null
        when (val result = coreRepository.getBranchById(branchId)) {
            is AppResult.Success -> branch = result.data
            is AppResult.Failure -> Unit
        }
        _uiState.update {
            it.copy(
                branchId = branchId,
                workshopId = branch?.workshopId,
                branchName = branch?.name.orEmpty()
            )
        }
        loadSchedule()
    }

    fun selectScope(scope: OperatingHoursScope) {
        if (scope == OperatingHoursScope.WORKSHOP && !_uiState.value.canEditWorkshop) return
        _uiState.update { it.copy(scope = scope) }
        loadSchedule()
    }

    fun updateDay(dayOfWeek: String, transform: (OperatingHoursDay) -> OperatingHoursDay) {
        _uiState.update { state ->
            state.copy(days = state.days.map { day -> if (day.dayOfWeek == dayOfWeek) transform(day) else day })
        }
    }

    fun saveSchedule() = viewModelScope.launch {
        val current = _uiState.value
        val id = selectedId(current) ?: return@launch
        _uiState.update { it.copy(isSaving = true, errorMessage = null, noticeMessage = null) }
        when (val result = repository.updateWeekly(current.scope, id, current.days)) {
            is AppResult.Success -> _uiState.update {
                it.copy(isSaving = false, days = result.data, noticeMessage = "Horario guardado.")
            }
            is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
        }
    }

    fun resetBranchHours() = viewModelScope.launch {
        val branchId = _uiState.value.branchId ?: return@launch
        _uiState.update { it.copy(isSaving = true, errorMessage = null, noticeMessage = null) }
        when (val result = repository.resetBranchHours(branchId)) {
            is AppResult.Success -> {
                _uiState.update { it.copy(scope = OperatingHoursScope.BRANCH, noticeMessage = "Esta sede ahora usa el horario del taller.") }
                loadSchedule()
            }
            is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
        }
    }

    fun addException(input: OperatingHoursExceptionInput) = viewModelScope.launch {
        val current = _uiState.value
        val id = selectedId(current) ?: return@launch
        _uiState.update { it.copy(isSaving = true, errorMessage = null, noticeMessage = null) }
        when (val result = repository.createException(current.scope, id, input)) {
            is AppResult.Success -> {
                _uiState.update { it.copy(isSaving = false, exceptions = (it.exceptions + result.data).sortedBy { item -> item.date }, noticeMessage = "Horario especial guardado.") }
            }
            is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
        }
    }

    fun deleteException(exception: OperatingHoursException) = viewModelScope.launch {
        val current = _uiState.value
        val id = selectedId(current) ?: return@launch
        _uiState.update { it.copy(isSaving = true, errorMessage = null, noticeMessage = null) }
        when (val result = repository.deleteException(current.scope, id, exception.id)) {
            is AppResult.Success -> _uiState.update { it.copy(isSaving = false, exceptions = it.exceptions.filterNot { item -> item.id == exception.id }, noticeMessage = "Excepción eliminada.") }
            is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.message) }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, noticeMessage = null) }

    private fun loadSchedule() = viewModelScope.launch {
        val current = _uiState.value
        val id = selectedId(current)
        if (id == null) {
            _uiState.update { it.copy(isLoading = false, days = defaultDays(), exceptions = emptyList()) }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val hoursResult = repository.getWeekly(current.scope, id)
        val exceptionsResult = repository.getExceptions(current.scope, id)
        val hours = (hoursResult as? AppResult.Success)?.data.orEmpty()
        val exceptions = (exceptionsResult as? AppResult.Success)?.data.orEmpty()
        val errors = listOfNotNull(
            (hoursResult as? AppResult.Failure)?.error?.message,
            (exceptionsResult as? AppResult.Failure)?.error?.message
        )
        _uiState.update {
            it.copy(
                isLoading = false,
                days = hours.ifEmpty(::defaultDays),
                exceptions = exceptions.sortedBy(OperatingHoursException::date),
                errorMessage = errors.takeIf { it.isNotEmpty() }?.joinToString("\n")
            )
        }
    }

    private fun selectedId(state: OperatingHoursUiState): String? = when (state.scope) {
        OperatingHoursScope.BRANCH -> state.branchId
        OperatingHoursScope.WORKSHOP -> state.workshopId
    }

    private fun defaultDays() = listOf(
        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    ).map { day ->
        val isOpen = day != "SUNDAY"
        OperatingHoursDay(day, isOpen, if (isOpen) "08:30" else null, if (isOpen) "18:00" else null)
    }
}
