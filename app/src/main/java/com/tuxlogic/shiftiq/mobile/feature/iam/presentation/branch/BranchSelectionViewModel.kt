package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase.SelectBranchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BranchItemUi(
    val id: String,
    val name: String,
    val address: String
)

data class BranchSelectionUiState(
    val branches: List<BranchItemUi> = listOf(
        BranchItemUi("b1000000-0000-0000-0000-000000000001", "Sede Central - ShiftIQ", "Av. Principal 1024, Lima"),
        BranchItemUi("b1000000-0000-0000-0000-000000000002", "Sede Norte - Taller Express", "Calle Los Pinos 450, Los Olivos"),
        BranchItemUi("b1000000-0000-0000-0000-000000000003", "Sede Sur - Mantenimiento Pesado", "Av. Panamericana Sur Km 18, Chorrillos")
    ),
    val selectedBranchId: String? = "b1000000-0000-0000-0000-000000000001",
    val isLoading: Boolean = false,
    val isConfirmed: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BranchSelectionViewModel @Inject constructor(
    private val selectBranchUseCase: SelectBranchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BranchSelectionUiState())
    val uiState: StateFlow<BranchSelectionUiState> = _uiState.asStateFlow()

    fun onBranchSelected(branchId: String) {
        _uiState.update { it.copy(selectedBranchId = branchId, errorMessage = null) }
    }

    fun confirmBranchSelection() {
        val selectedId = _uiState.value.selectedBranchId ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = selectBranchUseCase(BranchId(selectedId))
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isConfirmed = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}
