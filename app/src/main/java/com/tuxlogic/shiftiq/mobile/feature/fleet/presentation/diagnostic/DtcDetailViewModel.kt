package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleDtcAlertsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DtcDetailUiState(
    val dtcCode: String = "",
    val isLoading: Boolean = true,
    val alert: DtcAlert? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DtcDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val getVehicleDtcAlerts: GetVehicleDtcAlertsUseCase
) : ViewModel() {

    private val requestedCode: String =
        savedStateHandle.get<String>("dtcCode").orEmpty().ifBlank {
            savedStateHandle.get<String>("code").orEmpty()
        }

    private val _uiState = MutableStateFlow(DtcDetailUiState(dtcCode = requestedCode))
    val uiState: StateFlow<DtcDetailUiState> = _uiState.asStateFlow()

    init {
        loadAlert(requestedCode)
    }

    fun loadAlert(dtcCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, dtcCode = dtcCode, errorMessage = null) }
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            if (userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Your session expired. Please sign in again.")
                }
                return@launch
            }

            val customer = when (val result = getCustomerByUserId(userId)) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.message) }
                    return@launch
                }
            }

            val vehicles = when (val result = getCustomerVehicles(CustomerId(customer.id))) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.message) }
                    return@launch
                }
            }

            val vehicle = vehicles.firstOrNull()
            if (vehicle == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Register a vehicle to load OBD-II diagnostics.")
                }
                return@launch
            }

            val alerts = when (val result = getVehicleDtcAlerts(vehicle.id, page = 0, size = 50)) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.message) }
                    return@launch
                }
            }

            val match = alerts.firstOrNull {
                it.dtcCode.equals(dtcCode, ignoreCase = true)
            } ?: alerts.firstOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    alert = match,
                    dtcCode = match?.dtcCode ?: dtcCode,
                    errorMessage = if (match == null) {
                        "No live DTC alert was returned for this vehicle."
                    } else null
                )
            }
        }
    }
}

fun DtcSeverity.toDisplayLabel(): String = when (this) {
    DtcSeverity.LOW -> "Low"
    DtcSeverity.MEDIUM -> "Medium"
    DtcSeverity.HIGH -> "High"
    DtcSeverity.CRITICAL -> "Critical"
}
