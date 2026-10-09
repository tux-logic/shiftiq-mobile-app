package com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Branch
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.GetBranchesUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.SetActiveBranchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BranchManagementUiState(
    val workshopId: String = "",
    val isLoading: Boolean = false,
    val branches: List<Branch> = emptyList(),
    val activeBranchId: BranchId? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class BranchManagementViewModel @Inject constructor(
    private val getBranchesUseCase: GetBranchesUseCase,
    private val setActiveBranchUseCase: SetActiveBranchUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(BranchManagementUiState())
    val uiState: StateFlow<BranchManagementUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionDataStore.sessionState.collect { session ->
                _uiState.update { it.copy(activeBranchId = session.activeBranchId) }
            }
        }
    }

    fun loadBranches(workshopId: String) {
        _uiState.update { it.copy(workshopId = workshopId, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getBranchesUseCase(workshopId)
            result.onSuccess { list ->
                _uiState.update { it.copy(isLoading = false, branches = list) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun selectActiveBranch(branchId: BranchId) {
        viewModelScope.launch {
            setActiveBranchUseCase(branchId)
        }
    }
}
