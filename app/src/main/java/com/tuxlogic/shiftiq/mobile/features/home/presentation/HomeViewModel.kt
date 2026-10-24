package com.tuxlogic.shiftiq.mobile.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.features.home.application.GetHomeDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDashboardUseCase: GetHomeDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = getHomeDashboardUseCase(forceRefresh)
            result.fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isOffline = data.isOffline,
                            customerName = data.customerName,
                            isDongleConnected = data.isDongleConnected,
                            vehicle = data.vehicle,
                            metrics = data.metrics,
                            activeAlerts = data.activeAlerts,
                            activeService = data.activeService,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.message ?: "No se pudieron actualizar los datos del vehículo."
                        )
                    }
                }
            )
        }
    }

    fun onRetry() {
        loadDashboardData(forceRefresh = true)
    }

    fun onDismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
