package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.GetCustomerRegistrationsUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.operations.application.usecase.GetServicesUseCase
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class AppointmentCustomerOption(
    val customerId: String,
    val name: String,
    val phone: String,
    val vehicles: List<Vehicle>
)

data class CreateAppointmentUiState(
    val branchId: String = "",
    val customers: List<AppointmentCustomerOption> = emptyList(),
    val selectedCustomerId: String = "",
    val selectedVehicleId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val vehiclePlate: String = "",
    val vehicleModel: String = "",
    val selectedBayNumber: Int = 1,
    val services: List<ServiceItem> = emptyList(),
    val selectedServiceName: String = "",
    val isLoadingServices: Boolean = false,
    val scheduledStart: String = "",
    val notes: String = "",
    val isLoadingOptions: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CreateAppointmentViewModel @Inject constructor(
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val sessionDataStore: SessionDataStore,
    private val getCustomerRegistrations: GetCustomerRegistrationsUseCase,
    private val getCustomerById: GetCustomerByIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val getServices: GetServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CreateAppointmentUiState(scheduledStart = defaultScheduledStart())
    )
    val uiState: StateFlow<CreateAppointmentUiState> = _uiState.asStateFlow()
    private var requestedCustomerId: String? = null

    init {
        viewModelScope.launch {
            val branch = sessionDataStore.activeBranchId.firstOrNull()?.value?.toString().orEmpty()
            _uiState.update { it.copy(branchId = branch) }
            if (branch.isNotBlank()) loadOptions(branch)
            else _uiState.update {
                it.copy(isLoadingOptions = false, errorMessage = "Selecciona una sede activa para agendar una cita.")
            }
        }
    }

    fun onBranchIdChanged(value: String) {
        val normalized = value.trim()
        val changed = normalized != _uiState.value.branchId
        _uiState.update { it.copy(branchId = normalized, errorMessage = null) }
        if (changed && normalized.isNotBlank()) loadOptions(normalized)
    }

    /** A preselected customer may arrive before the directory has loaded. */
    fun onCustomerIdChanged(value: String) {
        val normalized = value.trim()
        requestedCustomerId = normalized.takeIf(String::isNotBlank)
        selectCustomer(normalized)
    }

    fun selectCustomer(customerId: String) {
        requestedCustomerId = customerId.takeIf(String::isNotBlank)
        val customer = _uiState.value.customers.firstOrNull { it.customerId == customerId }
        val vehicle = customer?.vehicles?.firstOrNull()
        _uiState.update {
            it.copy(
                selectedCustomerId = customer?.customerId.orEmpty(),
                selectedVehicleId = vehicle?.id?.toString().orEmpty(),
                customerName = customer?.name.orEmpty(),
                customerPhone = customer?.phone.orEmpty(),
                vehiclePlate = vehicle?.plateNumber?.value.orEmpty(),
                vehicleModel = vehicle?.let { listOf(it.brand, it.model, it.year.toString()).filter(String::isNotBlank).joinToString(" ") }.orEmpty(),
                errorMessage = null
            )
        }
    }

    fun onVehicleIdChanged(value: String) {
        val state = _uiState.value
        val vehicle = state.customers.firstOrNull { it.customerId == state.selectedCustomerId }
            ?.vehicles?.firstOrNull { it.id.toString() == value }
        _uiState.update {
            it.copy(
                selectedVehicleId = value,
                vehiclePlate = vehicle?.plateNumber?.value ?: it.vehiclePlate,
                vehicleModel = vehicle?.let { v -> listOf(v.brand, v.model, v.year.toString()).filter(String::isNotBlank).joinToString(" ") } ?: it.vehicleModel,
                errorMessage = null
            )
        }
    }

    fun onCustomerNameChanged(value: String) = _uiState.update { it.copy(customerName = value, errorMessage = null) }
    fun onCustomerPhoneChanged(value: String) = _uiState.update { it.copy(customerPhone = value, errorMessage = null) }
    fun onVehiclePlateChanged(value: String) = _uiState.update { it.copy(vehiclePlate = value.uppercase(), errorMessage = null) }
    fun onVehicleModelChanged(value: String) = _uiState.update { it.copy(vehicleModel = value, errorMessage = null) }
    fun onBaySelected(value: Int) = _uiState.update { it.copy(selectedBayNumber = value, errorMessage = null) }
    fun onServiceSelected(value: String) = _uiState.update { it.copy(selectedServiceName = value, errorMessage = null) }

    fun onScheduledStartChanged(value: String) {
        _uiState.update { it.copy(scheduledStart = value, errorMessage = null) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun refreshOptions() {
        _uiState.update { it.copy(isSuccess = false) }
        _uiState.value.branchId.takeIf(String::isNotBlank)?.let(::loadOptions)
    }

    fun prepareNewAppointment(
        branchId: String? = null,
        customerId: String? = null,
        scheduledDate: LocalDate? = null
    ) {
        requestedCustomerId = customerId?.takeIf(String::isNotBlank)
        val normalizedBranchId = branchId?.takeIf(String::isNotBlank)
        _uiState.update {
            it.copy(
                branchId = normalizedBranchId ?: it.branchId,
                selectedCustomerId = "",
                selectedVehicleId = "",
                customerName = "",
                customerPhone = "",
                vehiclePlate = "",
                vehicleModel = "",
                selectedBayNumber = 1,
                selectedServiceName = it.services.firstOrNull()?.name.orEmpty(),
                scheduledStart = defaultScheduledStart(scheduledDate),
                notes = "",
                isSuccess = false,
                errorMessage = null
            )
        }
        refreshOptions()
    }

    private fun loadOptions(branchId: String) = viewModelScope.launch {
        val branchUuid = runCatching { UUID.fromString(branchId) }.getOrNull()
        if (branchUuid == null) {
            _uiState.update { it.copy(isLoadingOptions = false, isLoadingServices = false, errorMessage = "La sede activa no tiene un identificador válido.") }
            return@launch
        }

        _uiState.update { it.copy(isLoadingOptions = true, errorMessage = null) }
        _uiState.update { it.copy(isLoadingServices = true) }
        val registrations = getCustomerRegistrations(branchId = branchUuid, status = "ACTIVE")
        val customers = when (registrations) {
            is AppResult.Failure -> {
                _uiState.update { it.copy(isLoadingOptions = false, errorMessage = registrations.error.message) }
                emptyList()
            }
            is AppResult.Success -> supervisorScope {
                registrations.data.map { registration ->
                    async {
                        val customerId = registration.customerId.toString()
                        val profile = (getCustomerById(customerId) as? AppResult.Success)?.data
                        val vehicles = (getCustomerVehicles(CustomerId(customerId)) as? AppResult.Success)?.data.orEmpty()
                        AppointmentCustomerOption(
                            customerId = customerId,
                            name = profile?.displayName?.takeIf(String::isNotBlank) ?: "Cliente ${customerId.take(8)}",
                            phone = profile?.phone.orEmpty(),
                            vehicles = vehicles
                        )
                    }
                }.awaitAll().sortedBy { it.name.lowercase() }
            }
        }

        val serviceResult = getServices(BranchId(branchUuid))
        val services = (serviceResult as? AppResult.Success)?.data.orEmpty()
        val current = _uiState.value
        val preferredCustomer = requestedCustomerId ?: current.selectedCustomerId
        val selectedCustomer = customers.firstOrNull { it.customerId == preferredCustomer }
        val selectedVehicle = selectedCustomer?.vehicles
            ?.firstOrNull { it.id.toString() == current.selectedVehicleId }
            ?: selectedCustomer?.vehicles?.firstOrNull()
        val selectedVehicleId = selectedVehicle?.id?.toString().orEmpty()

        _uiState.update {
            it.copy(
                branchId = branchId,
                customers = customers,
                selectedCustomerId = selectedCustomer?.customerId.orEmpty(),
                selectedVehicleId = selectedVehicleId,
                customerName = selectedCustomer?.name ?: current.customerName,
                customerPhone = selectedCustomer?.phone ?: current.customerPhone,
                vehiclePlate = selectedVehicle?.plateNumber?.value ?: current.vehiclePlate,
                vehicleModel = selectedVehicle?.let { vehicle -> listOf(vehicle.brand, vehicle.model, vehicle.year.toString()).filter(String::isNotBlank).joinToString(" ") } ?: current.vehicleModel,
                services = services,
                selectedServiceName = services.firstOrNull { it.name == current.selectedServiceName }?.name
                    ?: services.firstOrNull()?.name.orEmpty(),
                isLoadingServices = false,
                isLoadingOptions = false,
                errorMessage = it.errorMessage
            )
        }
    }

    fun createAppointment() {
        val currentState = _uiState.value
        val branchUuid = runCatching { UUID.fromString(currentState.branchId) }.getOrNull()
        val customer = currentState.customers.firstOrNull { it.customerId == currentState.selectedCustomerId }
        val vehicle = customer?.vehicles?.firstOrNull { it.id.toString() == currentState.selectedVehicleId }
        val scheduledStart = currentState.scheduledStart.trim()

        when {
            branchUuid == null -> _uiState.update { it.copy(errorMessage = "Selecciona una sede activa para agendar una cita.") }
            currentState.customerName.isBlank() -> _uiState.update { it.copy(errorMessage = "Ingrese el cliente.") }
            currentState.vehiclePlate.isBlank() -> _uiState.update { it.copy(errorMessage = "Ingrese la placa del vehículo.") }
            currentState.services.isNotEmpty() && currentState.selectedServiceName.isBlank() -> _uiState.update { it.copy(errorMessage = "Seleccione un servicio.") }
            scheduledStart.isBlank() -> _uiState.update { it.copy(errorMessage = "Selecciona la fecha y hora de la cita.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                when (
                    val result = createAppointmentUseCase(
                        branchId = branchUuid,
                        customerId = customer?.customerId?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: UUID.randomUUID(),
                        vehicleId = vehicle?.id?.value ?: UUID.randomUUID(),
                        scheduledStart = scheduledStart,
                        notes = currentState.notes.trim().takeIf(String::isNotBlank)
                    )
                ) {
                    is AppResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    is AppResult.Failure -> _uiState.update { it.copy(isLoading = false, errorMessage = result.error.message) }
                }
            }
        }
    }

    private companion object {
        fun defaultScheduledStart(date: LocalDate? = null): String = (date ?: LocalDate.now())
            .atTime(9, 0)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    }
}
