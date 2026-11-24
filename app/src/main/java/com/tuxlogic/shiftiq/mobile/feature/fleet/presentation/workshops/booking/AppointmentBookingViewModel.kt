package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchCatalogUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles.GetCustomerVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppointmentBookingViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val getBranchCatalog: GetBranchCatalogUseCase,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val createAppointment: CreateAppointmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workshopId: String = savedStateHandle["workshopId"] ?: ""

    private val _uiState = MutableStateFlow(AppointmentBookingUiState(workshopId = workshopId))
    val uiState: StateFlow<AppointmentBookingUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val catalog = when (val result = getBranchCatalog()) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> emptyList()
            }
            val catalogItem = catalog.firstOrNull {
                it.branchId == workshopId || it.workshopId == workshopId
            }
            val alternatives = catalog
                .filterNot { it.branchId == catalogItem?.branchId }
                .take(5)
                .map {
                    AlternativeBranch(
                        id = it.branchId,
                        name = it.displayName,
                        address = it.address,
                        activeBaysCount = 0
                    )
                }

            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            val vehicle = if (userId != null) {
                val customer = (getCustomerByUserId(userId) as? AppResult.Success)?.data
                val vehicles = customer?.let {
                    (getCustomerVehicles(CustomerId(it.id)) as? AppResult.Success)?.data.orEmpty()
                }.orEmpty()
                vehicles.firstOrNull()?.let {
                    B2CVehicleItem(
                        id = it.id.toString(),
                        brand = it.brand,
                        model = it.model,
                        year = it.year,
                        plateNumber = it.plateNumber.value,
                        vin = it.vin.value,
                        photoUrl = it.photoUrl,
                        isMonitored = false,
                        batteryVoltage = null,
                        isPrimary = true
                    )
                }
            } else null

            _uiState.update {
                it.copy(
                    workshopId = catalogItem?.branchId ?: workshopId,
                    workshopName = catalogItem?.displayName.orEmpty().ifBlank { "Workshop" },
                    workshopAddress = catalogItem?.address.orEmpty(),
                    workshopDistrict = catalogItem?.address
                        ?.substringAfterLast(",")
                        ?.trim()
                        .orEmpty(),
                    selectedVehicle = vehicle,
                    alternativeBranches = alternatives
                )
            }
        }
    }

    fun onSelectDay(index: Int) {
        _uiState.update { it.copy(selectedDayIndex = index) }
    }

    fun onSelectBay(bayId: String) {
        _uiState.update { it.copy(selectedBayId = bayId) }
    }

    fun onSelectSlot(index: Int) {
        _uiState.update { it.copy(selectedSlotIndex = index) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(reasonNotes = notes) }
    }

    fun onOpenConfirmationModal() {
        _uiState.update { it.copy(showConfirmationModal = true) }
    }

    fun onDismissConfirmationModal() {
        _uiState.update { it.copy(showConfirmationModal = false) }
    }

    fun onTriggerConflictSimulation() {
        _uiState.update { it.copy(showConflictErrorDialog = true, showConfirmationModal = false) }
    }

    fun onDismissConflictDialog() {
        _uiState.update { it.copy(showConflictErrorDialog = false) }
    }

    fun onTriggerBranchUnavailableSimulation() {
        _uiState.update { it.copy(showBranchUnavailableDialog = true) }
    }

    fun onDismissBranchUnavailableDialog() {
        _uiState.update { it.copy(showBranchUnavailableDialog = false) }
    }

    fun onSelectAlternativeBranch(branch: AlternativeBranch) {
        _uiState.update {
            it.copy(
                showBranchUnavailableDialog = false,
                workshopId = branch.id,
                workshopName = branch.name,
                workshopAddress = branch.address
            )
        }
    }

    fun confirmBooking() {
        if (_uiState.value.isBookingLoading) return
        val booking = _uiState.value
        val vehicle = booking.selectedVehicle
        if (vehicle == null) {
            _uiState.update { it.copy(errorMessage = "Register a vehicle before booking.") }
            return
        }

        _uiState.update { it.copy(isBookingLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            val customer = userId?.let { (getCustomerByUserId(it) as? AppResult.Success)?.data }
            if (customer == null) {
                _uiState.update {
                    it.copy(isBookingLoading = false, errorMessage = "Customer profile was not found.")
                }
                return@launch
            }

            val day = booking.availableDays.getOrNull(booking.selectedDayIndex).orEmpty()
            val slot = booking.availableSlots.getOrNull(booking.selectedSlotIndex).orEmpty()
            val scheduledStart = resolveScheduledStart(day, slot)
            if (scheduledStart == null) {
                _uiState.update {
                    it.copy(isBookingLoading = false, errorMessage = "Select a valid date and time.")
                }
                return@launch
            }

            val branchUuid = runCatching { UUID.fromString(booking.workshopId) }.getOrNull()
            if (branchUuid == null) {
                _uiState.update {
                    it.copy(isBookingLoading = false, errorMessage = "Invalid workshop branch identifier.")
                }
                return@launch
            }

            when (
                val result = createAppointment(
                    branchId = branchUuid,
                    customerId = UUID.fromString(customer.id),
                    vehicleId = UUID.fromString(vehicle.id),
                    scheduledStart = scheduledStart,
                    notes = booking.reasonNotes.ifBlank { null }
                )
            ) {
                is AppResult.Failure -> {
                    val message = result.error.message
                    val isConflict = message.contains("conflict", ignoreCase = true) ||
                        message.contains("overlap", ignoreCase = true) ||
                        message.contains("already", ignoreCase = true)
                    _uiState.update {
                        it.copy(
                            isBookingLoading = false,
                            showConfirmationModal = false,
                            showConflictErrorDialog = isConflict,
                            errorMessage = if (isConflict) null else message
                        )
                    }
                }

                is AppResult.Success -> {
                    val appointment = result.data
                    val selectedBay = booking.availableBays.firstOrNull { it.id == booking.selectedBayId }
                    val voucher = ConfirmedAppointmentVoucher(
                        reservationCode = appointment.id.toString().take(8).uppercase(),
                        workshopName = booking.workshopName,
                        district = booking.workshopDistrict,
                        scheduledDate = day,
                        scheduledTime = slot,
                        bayName = selectedBay?.name.orEmpty(),
                        serviceTitle = booking.reasonNotes,
                        vehiclePlate = vehicle.plateNumber,
                        estimatedPen = booking.estimatedPricePen
                    )
                    _uiState.update {
                        it.copy(
                            isBookingLoading = false,
                            showConfirmationModal = false,
                            successVoucher = voucher
                        )
                    }
                }
            }
        }
    }

    private fun resolveScheduledStart(dateLabel: String, timeLabel: String): String? {
        if (dateLabel.isBlank() || timeLabel.isBlank()) return null
        return runCatching {
            val today = LocalDate.now()
            val date = when {
                dateLabel.contains("Hoy", ignoreCase = true) || dateLabel.contains("Today", ignoreCase = true) -> today
                dateLabel.contains("Mañana", ignoreCase = true) || dateLabel.contains("Tomorrow", ignoreCase = true) -> today.plusDays(1)
                else -> LocalDate.parse(dateLabel.trim(), DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "PE")))
            }
            val time = LocalTime.parse(
                timeLabel.trim().uppercase(Locale.US),
                DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            )
            LocalDateTime.of(date, time).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }.getOrNull()
    }

    fun onDismissSuccessVoucher() {
        _uiState.update { it.copy(successVoucher = null) }
    }
}
