package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchesUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetWorkshopsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.SelectBranchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BranchItemUi(
    val id: String,
    val name: String,
    val address: String
)

data class BranchSelectionUiState(
    val branches: List<BranchItemUi> = emptyList(),
    val selectedBranchId: String? = null,
    val isLoading: Boolean = true,
    val isConfirmed: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BranchSelectionViewModel @Inject constructor(
    private val selectBranchUseCase: SelectBranchUseCase,
    private val sessionDataStore: SessionDataStore,
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase,
    private val getWorkshopsUseCase: GetWorkshopsUseCase,
    private val getBranchesUseCase: GetBranchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BranchSelectionUiState())
    val uiState: StateFlow<BranchSelectionUiState> = _uiState.asStateFlow()

    init {
        loadBranches()
    }

    fun loadBranches() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            if (userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Your session expired. Please sign in again."
                    )
                }
                return@launch
            }

            when (val ownerResult = getOwnerProfileUseCase(userId)) {
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = ownerResult.error.message)
                    }
                }

                is AppResult.Success -> {
                    when (val workshopsResult = getWorkshopsUseCase(ownerResult.data.id)) {
                        is AppResult.Failure -> {
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = workshopsResult.error.message)
                            }
                        }

                        is AppResult.Success -> {
                            val branches = mutableListOf<BranchItemUi>()
                            var loadError: String? = null
                            for (workshop in workshopsResult.data) {
                                when (val branchResult = getBranchesUseCase(workshop.id)) {
                                    is AppResult.Failure -> loadError = branchResult.error.message
                                    is AppResult.Success -> {
                                        branches += branchResult.data.map { branch ->
                                            BranchItemUi(
                                                id = branch.id.toString(),
                                                name = "${workshop.brandName} · ${branch.name}",
                                                address = branch.address
                                            )
                                        }
                                    }
                                }
                            }

                            val activeBranchId = sessionDataStore.activeBranchId.firstOrNull()?.toString()
                            val selectedId = when {
                                branches.any { it.id == activeBranchId } -> activeBranchId
                                else -> branches.firstOrNull()?.id
                            }

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    branches = branches,
                                    selectedBranchId = selectedId,
                                    errorMessage = when {
                                        branches.isEmpty() && loadError != null -> loadError
                                        branches.isEmpty() -> "No branches were found for your workshops. Create a branch first."
                                        else -> null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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
