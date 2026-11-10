package com.tuxlogic.shiftiq.mobile.feature.iot.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleDtcAlertsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.telemetry.GetVehicleTelemetryHistoryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.DeleteVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.RegisterVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.UpdateVehicleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerVehiclesUiState(
    val isLoading: Boolean = true,
    val customerName: String? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val isAddingVehicle: Boolean = false,
    val isEditingVehicle: Boolean = false,
    val isRegisteringVehicle: Boolean = false,
    val vehiclePlateInput: String = "",
    val vehicleBrandInput: String = "",
    val vehicleModelInput: String = "",
    val vehicleYearInput: String = "",
    val vehicleVinInput: String = "",
    val vehicleRegistrationError: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val isDeletingVehicle: Boolean = false,
    val vehicleDeleteErrorMessage: String? = null,
    val telemetryByVehicleId: Map<String, TelemetrySnapshot?> = emptyMap(),
    val isLoadingVehicleTelemetry: Boolean = false,
    val selectedVehicle: Vehicle? = null,
    val isLoadingDetails: Boolean = false,
    val latestTelemetry: TelemetrySnapshot? = null,
    val telemetryHistory: List<TelemetrySnapshot> = emptyList(),
    val telemetryHistoryPage: Int = 0,
    val hasMoreTelemetryHistory: Boolean = false,
    val isLoadingMoreTelemetryHistory: Boolean = false,
    val telemetryHistoryErrorMessage: String? = null,
    val dtcAlerts: List<DtcAlert> = emptyList(),
    val errorMessage: String? = null,
    val detailsErrorMessage: String? = null
)

@HiltViewModel
class CustomerVehiclesViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val getVehicleTelemetryHistory: GetVehicleTelemetryHistoryUseCase,
    private val getVehicleDtcAlerts: GetVehicleDtcAlertsUseCase,
    private val registerVehicle: RegisterVehicleUseCase,
    private val updateVehicle: UpdateVehicleUseCase,
    private val deleteVehicle: DeleteVehicleUseCase
) : ViewModel() {

    private companion object {
        const val TELEMETRY_PAGE_SIZE = 20
    }

    private val _uiState = MutableStateFlow(CustomerVehiclesUiState())
    val uiState: StateFlow<CustomerVehiclesUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    fun startVehicleRegistration() {
        _uiState.update {
            it.copy(
                isAddingVehicle = true,
                isEditingVehicle = false,
                vehiclePlateInput = "",
                vehicleBrandInput = "",
                vehicleModelInput = "",
                vehicleYearInput = "",
                vehicleVinInput = "",
                vehicleRegistrationError = null
            )
        }
    }

    fun startVehicleEditing(vehicle: Vehicle) {
        _uiState.update {
            it.copy(
                isAddingVehicle = true,
                isEditingVehicle = true,
                vehiclePlateInput = vehicle.plateNumber.value,
                vehicleBrandInput = vehicle.brand,
                vehicleModelInput = vehicle.model,
                vehicleYearInput = vehicle.year.toString(),
                vehicleVinInput = vehicle.vin.value,
                vehicleRegistrationError = null
            )
        }
    }

    fun cancelVehicleRegistration() {
        _uiState.update {
            it.copy(
                isAddingVehicle = false,
                isEditingVehicle = false,
                isRegisteringVehicle = false,
                vehicleRegistrationError = null
            )
        }
    }

    fun onVehiclePlateChanged(value: String) {
        _uiState.update { it.copy(vehiclePlateInput = value.take(10).uppercase(), vehicleRegistrationError = null) }
    }

    fun onVehicleBrandChanged(value: String) {
        _uiState.update { it.copy(vehicleBrandInput = value, vehicleRegistrationError = null) }
    }

    fun onVehicleModelChanged(value: String) {
        _uiState.update { it.copy(vehicleModelInput = value, vehicleRegistrationError = null) }
    }

    fun onVehicleYearChanged(value: String) {
        _uiState.update {
            it.copy(vehicleYearInput = value.filter(Char::isDigit).take(4), vehicleRegistrationError = null)
        }
    }

    fun onVehicleVinChanged(value: String) {
        _uiState.update { it.copy(vehicleVinInput = value.filter(Char::isLetterOrDigit).take(17).uppercase(), vehicleRegistrationError = null) }
    }

    fun submitVehicleRegistration() {
        val form = _uiState.value
        val vehicleToUpdate = form.selectedVehicle.takeIf { form.isEditingVehicle }
        val year = form.vehicleYearInput.toIntOrNull()
        val validationMessage = when {
            form.vehicleBrandInput.isBlank() -> "Escribe la marca del vehículo."
            form.vehicleModelInput.isBlank() -> "Escribe el modelo del vehículo."
            !PlateNumber.isValid(form.vehiclePlateInput) -> "La placa debe tener entre 5 y 10 caracteres."
            year == null || year !in 1900..2100 -> "Ingresa un año válido entre 1900 y 2100."
            !Vin.isValid(form.vehicleVinInput) -> "El VIN debe tener 17 caracteres y no puede incluir I, O ni Q."
            else -> null
        }
        if (validationMessage != null) {
            _uiState.update { it.copy(vehicleRegistrationError = validationMessage) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRegisteringVehicle = true, vehicleRegistrationError = null) }
            val result = if (vehicleToUpdate != null) {
                updateVehicle(
                    id = vehicleToUpdate.id,
                    plateNumber = form.vehiclePlateInput.trim(),
                    brand = form.vehicleBrandInput.trim(),
                    model = form.vehicleModelInput.trim(),
                    year = year!!,
                    vin = form.vehicleVinInput.trim(),
                    photoUrl = vehicleToUpdate.photoUrl
                )
            } else {
                registerVehicle(
                    plateNumber = form.vehiclePlateInput.trim(),
                    brand = form.vehicleBrandInput.trim(),
                    model = form.vehicleModelInput.trim(),
                    year = year!!,
                    vin = form.vehicleVinInput.trim()
                )
            }

            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAddingVehicle = false,
                            isEditingVehicle = false,
                            isRegisteringVehicle = false,
                            selectedVehicle = vehicleToUpdate?.let { result.data } ?: it.selectedVehicle,
                            vehicleRegistrationError = null,
                            vehiclePlateInput = "",
                            vehicleBrandInput = "",
                            vehicleModelInput = "",
                            vehicleYearInput = "",
                            vehicleVinInput = ""
                        )
                    }
                    loadVehicles()
                }

                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isRegisteringVehicle = false,
                            vehicleRegistrationError = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun requestDeleteVehicleConfirmation() {
        if (_uiState.value.selectedVehicle == null) return
        _uiState.update {
            it.copy(showDeleteConfirmation = true, vehicleDeleteErrorMessage = null)
        }
    }

    fun dismissDeleteVehicleConfirmation() {
        if (_uiState.value.isDeletingVehicle) return
        _uiState.update {
            it.copy(showDeleteConfirmation = false, vehicleDeleteErrorMessage = null)
        }
    }

    fun deleteSelectedVehicle() {
        val vehicle = _uiState.value.selectedVehicle ?: return
        if (_uiState.value.isDeletingVehicle) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingVehicle = true, vehicleDeleteErrorMessage = null) }
            when (val result = deleteVehicle(vehicle.id)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            selectedVehicle = null,
                            latestTelemetry = null,
                            telemetryHistory = emptyList(),
                            telemetryHistoryPage = 0,
                            hasMoreTelemetryHistory = false,
                            isLoadingMoreTelemetryHistory = false,
                            telemetryHistoryErrorMessage = null,
                            dtcAlerts = emptyList(),
                            detailsErrorMessage = null,
                            showDeleteConfirmation = false,
                            isDeletingVehicle = false,
                            vehicleDeleteErrorMessage = null
                        )
                    }
                    loadVehicles()
                }

                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isDeletingVehicle = false,
                            vehicleDeleteErrorMessage = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun loadVehicles() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, isLoadingVehicleTelemetry = false, errorMessage = null)
            }

            val userId = sessionDataStore.userId.firstOrNull()
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Customer session was not found. Please sign in again."
                    )
                }
                return@launch
            }

            when (val profileResult = getCustomerByUserId(userId.toString())) {
                is AppResult.Success -> {
                    val customer = profileResult.data
                    _uiState.update { it.copy(customerName = customer.displayName) }

                    when (val vehiclesResult = getCustomerVehicles(CustomerId(customer.id))) {
                        is AppResult.Success -> {
                            val vehicles = vehiclesResult.data
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    vehicles = vehicles,
                                    telemetryByVehicleId = emptyMap(),
                                    isLoadingVehicleTelemetry = vehicles.isNotEmpty(),
                                    errorMessage = null
                                )
                            }

                            if (vehicles.isNotEmpty()) {
                                val telemetryByVehicleId = coroutineScope {
                                    vehicles.map { vehicle ->
                                        async {
                                            val result = getVehicleTelemetryHistory(vehicle.id, size = 1)
                                            vehicle.id.toString() to
                                                (result as? AppResult.Success)?.data?.firstOrNull()
                                        }
                                    }.awaitAll().toMap()
                                }
                                _uiState.update {
                                    it.copy(
                                        telemetryByVehicleId = telemetryByVehicleId,
                                        isLoadingVehicleTelemetry = false
                                    )
                                }
                            }
                        }

                        is AppResult.Failure -> {
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = vehiclesResult.error.message)
                            }
                        }
                    }
                }

                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = profileResult.error.message)
                    }
                }
            }
        }
    }

    fun selectVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedVehicle = vehicle,
                    isLoadingDetails = true,
                    latestTelemetry = null,
                    telemetryHistory = emptyList(),
                    telemetryHistoryPage = 0,
                    hasMoreTelemetryHistory = false,
                    isLoadingMoreTelemetryHistory = false,
                    telemetryHistoryErrorMessage = null,
                    dtcAlerts = emptyList(),
                    detailsErrorMessage = null
                )
            }

            val (telemetryResult, alertsResult) = coroutineScope {
                val telemetry = async {
                    getVehicleTelemetryHistory(vehicle.id, page = 0, size = TELEMETRY_PAGE_SIZE)
                }
                val alerts = async { getVehicleDtcAlerts(vehicle.id) }
                telemetry.await() to alerts.await()
            }

            val telemetryPage = (telemetryResult as? AppResult.Success)?.data.orEmpty()
            val telemetryHistory = telemetryPage.sortedBy { it.createdAt }
            val telemetry = telemetryHistory.lastOrNull()
            val alerts = (alertsResult as? AppResult.Success)?.data.orEmpty()
            val detailsError = listOfNotNull(
                (telemetryResult as? AppResult.Failure)?.error?.message,
                (alertsResult as? AppResult.Failure)?.error?.message
            ).distinct().joinToString(" ").ifBlank { null }

            _uiState.update {
                it.copy(
                    isLoadingDetails = false,
                    latestTelemetry = telemetry,
                    telemetryHistory = telemetryHistory,
                    telemetryHistoryPage = 0,
                    hasMoreTelemetryHistory = telemetryPage.size == TELEMETRY_PAGE_SIZE,
                    isLoadingMoreTelemetryHistory = false,
                    telemetryHistoryErrorMessage = null,
                    dtcAlerts = alerts,
                    detailsErrorMessage = detailsError
                )
            }
        }
    }

    fun loadMoreTelemetryHistory() {
        val currentState = _uiState.value
        val vehicle = currentState.selectedVehicle ?: return
        if (currentState.isLoadingDetails || currentState.isLoadingMoreTelemetryHistory || !currentState.hasMoreTelemetryHistory) {
            return
        }

        val nextPage = currentState.telemetryHistoryPage + 1
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoadingMoreTelemetryHistory = true, telemetryHistoryErrorMessage = null)
            }

            when (
                val result = getVehicleTelemetryHistory(
                    vehicle.id,
                    page = nextPage,
                    size = TELEMETRY_PAGE_SIZE
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update { state ->
                        if (state.selectedVehicle?.id != vehicle.id) {
                            state
                        } else {
                            val mergedHistory = (state.telemetryHistory + result.data)
                                .distinctBy { it.id }
                                .sortedBy { it.createdAt }
                            state.copy(
                                telemetryHistory = mergedHistory,
                                latestTelemetry = mergedHistory.lastOrNull() ?: state.latestTelemetry,
                                telemetryHistoryPage = nextPage,
                                hasMoreTelemetryHistory = result.data.size == TELEMETRY_PAGE_SIZE,
                                isLoadingMoreTelemetryHistory = false
                            )
                        }
                    }
                }

                is AppResult.Failure -> {
                    _uiState.update { state ->
                        if (state.selectedVehicle?.id != vehicle.id) {
                            state
                        } else {
                            state.copy(
                                isLoadingMoreTelemetryHistory = false,
                                telemetryHistoryErrorMessage = result.error.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearSelectedVehicle() {
        _uiState.update {
            it.copy(
                selectedVehicle = null,
                latestTelemetry = null,
                telemetryHistory = emptyList(),
                telemetryHistoryPage = 0,
                hasMoreTelemetryHistory = false,
                isLoadingMoreTelemetryHistory = false,
                telemetryHistoryErrorMessage = null,
                dtcAlerts = emptyList(),
                detailsErrorMessage = null
            )
        }
    }

    fun refreshSelectedVehicle() {
        _uiState.value.selectedVehicle?.let(::selectVehicle)
    }
}
