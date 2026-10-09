package com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.onFailure
import com.tuxlogic.shiftiq.mobile.core.common.result.onSuccess
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.Workshop
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.GetOwnerProfileUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.usecase.GetWorkshopsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkshopListUiState(
    val isLoading: Boolean = false,
    val workshops: List<Workshop> = emptyList(),
    val ownerId: String? = null,
    val needsOwnerProfile: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class WorkshopListViewModel @Inject constructor(
    private val getWorkshopsUseCase: GetWorkshopsUseCase,
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopListUiState())
    val uiState: StateFlow<WorkshopListUiState> = _uiState.asStateFlow()

    init {
        loadWorkshops()
    }

    fun loadWorkshops() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val session = sessionDataStore.sessionState.first()
            val userId = session.userId?.toString()

            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sesión no disponible") }
                return@launch
            }

            // Consultamos perfil de dueño para obtener ownerId
            val ownerResult = getOwnerProfileUseCase(userId)
            ownerResult.onSuccess { owner ->
                _uiState.update { it.copy(ownerId = owner.id, needsOwnerProfile = false) }
                val workshopsResult = getWorkshopsUseCase(owner.id)
                workshopsResult.onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, workshops = list) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            }.onFailure {
                // Si el dueño aún no existe, el usuario debe crear su perfil de dueño
                _uiState.update { it.copy(isLoading = false, needsOwnerProfile = true) }
            }
        }
    }
}
