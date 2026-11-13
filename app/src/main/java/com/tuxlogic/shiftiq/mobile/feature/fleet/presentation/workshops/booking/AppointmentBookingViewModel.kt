package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppointmentBookingViewModel @Inject constructor(
    private val vehicleDao: VehicleDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workshopId: String = savedStateHandle["workshopId"] ?: "w1"

    private val _uiState = MutableStateFlow(AppointmentBookingUiState(workshopId = workshopId))
    val uiState: StateFlow<AppointmentBookingUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val cachedVehicles = vehicleDao.getAllVehicles().firstOrNull().orEmpty()
            val vehicle = cachedVehicles.firstOrNull()?.let {
                B2CVehicleItem(
                    id = it.id,
                    brand = it.brand,
                    model = it.model,
                    year = it.year,
                    plateNumber = it.plateNumber,
                    vin = it.vin,
                    photoUrl = it.photoUrl,
                    isMonitored = true,
                    batteryVoltage = 13.8,
                    isPrimary = it.isPrimary
                )
            } ?: B2CVehicleItem(
                id = UUID.randomUUID().toString(),
                brand = "Kia",
                model = "Rio",
                year = 2019,
                plateNumber = "ABC-123",
                vin = "9BWCA21102P012984",
                isMonitored = true,
                batteryVoltage = 13.8,
                isPrimary = true
            )

            val name = if (workshopId == "w2") "AutoService Express - Mecánica Rápida" else "Taller Automotriz Silva - Sede Central"
            val address = if (workshopId == "w2") "Av. Sucre 820 • Pueblo Libre" else "Av. La Marina 2450 • San Miguel"
            val district = if (workshopId == "w2") "Pueblo Libre" else "San Miguel"

            _uiState.update {
                it.copy(
                    workshopName = name,
                    workshopAddress = address,
                    workshopDistrict = district,
                    selectedVehicle = vehicle
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
                workshopName = branch.name,
                workshopAddress = branch.address
            )
        }
    }

    fun confirmBooking() {
        if (_uiState.value.isBookingLoading) return
        val booking = _uiState.value
        _uiState.update { it.copy(isBookingLoading = true) }

        viewModelScope.launch {
            delay(450)
            // Demo local: no se envía la reserva al backend.
            val voucherCode = "RES-${booking.selectedBayId.uppercase()}-${UUID.randomUUID().toString().take(6).uppercase()}"
            val day = booking.availableDays.getOrNull(booking.selectedDayIndex).orEmpty()
            val slot = booking.availableSlots.getOrNull(booking.selectedSlotIndex).orEmpty()
            val selectedBay = booking.availableBays.firstOrNull { it.id == booking.selectedBayId }
            val vehicle = booking.selectedVehicle

            val voucher = ConfirmedAppointmentVoucher(
                reservationCode = voucherCode,
                workshopName = booking.workshopName,
                district = booking.workshopDistrict,
                scheduledDate = day,
                scheduledTime = slot,
                bayName = selectedBay?.name ?: "Bahía 2 (Elevador Hidráulico)",
                serviceTitle = booking.reasonNotes,
                vehiclePlate = vehicle?.plateNumber ?: "ABC-123",
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

    fun onDismissSuccessVoucher() {
        _uiState.update { it.copy(successVoucher = null) }
    }
}
