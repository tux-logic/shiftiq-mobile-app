package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppointmentBookingViewModel @Inject constructor(
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val vehicleDao: VehicleDao,
    private val sessionDataStore: SessionDataStore,
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
        _uiState.update { it.copy(isBookingLoading = true) }

        viewModelScope.launch {
            val session = sessionDataStore.sessionState.firstOrNull()
            val customerId = session?.userId?.value ?: UUID.randomUUID()
            val branchId = session?.activeBranchId?.value ?: UUID.randomUUID()
            val vehicle = _uiState.value.selectedVehicle
            val vehicleId = try {
                UUID.fromString(vehicle?.id)
            } catch (_: Exception) {
                UUID.randomUUID()
            }

            val result = createAppointmentUseCase(
                branchId = branchId,
                customerId = customerId,
                vehicleId = vehicleId,
                scheduledStart = "2026-11-12T15:30:00Z",
                notes = _uiState.value.reasonNotes
            )

            if (result is AppResult.Failure && result.error is AppError.Conflict) {
                // 409 Conflict handled as requested in Phase 2
                _uiState.update {
                    it.copy(
                        isBookingLoading = false,
                        showConfirmationModal = false,
                        showConflictErrorDialog = true
                    )
                }
                return@launch
            }

            val voucherCode = if (result is AppResult.Success) {
                "RES-${_uiState.value.selectedBayId.uppercase()}-${result.data.id.toString().take(6).uppercase()}"
            } else {
                "RES-BAY2-02-2026"
            }

            val day = _uiState.value.availableDays[_uiState.value.selectedDayIndex]
            val slot = _uiState.value.availableSlots[_uiState.value.selectedSlotIndex]
            val selectedBay = _uiState.value.availableBays.firstOrNull { it.id == _uiState.value.selectedBayId }

            val voucher = ConfirmedAppointmentVoucher(
                reservationCode = voucherCode,
                workshopName = _uiState.value.workshopName,
                district = _uiState.value.workshopDistrict,
                scheduledDate = day,
                scheduledTime = slot,
                bayName = selectedBay?.name ?: "Bahía 2 (Elevador Hidráulico)",
                serviceTitle = _uiState.value.reasonNotes,
                vehiclePlate = vehicle?.plateNumber ?: "ABC-123",
                estimatedPen = _uiState.value.estimatedPricePen
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
