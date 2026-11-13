package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.WorkshopItem
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
class WorkshopsViewModel @Inject constructor(
    private val vehicleDao: VehicleDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopsUiState())
    val uiState: StateFlow<WorkshopsUiState> = _uiState.asStateFlow()

    init {
        loadWorkshopsAndVehicle()
    }

    private fun loadWorkshopsAndVehicle() {
        val seedWorkshops = listOf(
            WorkshopItem(
                id = "w1",
                name = "Taller Silva - Especialistas en Inyección",
                rating = 4.8,
                reviewCount = 115,
                district = "San Miguel",
                distanceKm = 1.2,
                isCertifiedShiftIQ = true,
                hasFreeBaysToday = true,
                estimatedCostMinPen = 140.0,
                estimatedCostMaxPen = 180.0,
                nextAvailableSlot = "Hoy, 3:30 PM",
                linkedIssue = "Sensor O2 Banco 1"
            ),
            WorkshopItem(
                id = "w2",
                name = "AutoService Express - Mecánica Rápida",
                rating = 4.6,
                reviewCount = 89,
                district = "Pueblo Libre",
                distanceKm = 2.4,
                isCertifiedShiftIQ = true,
                hasFreeBaysToday = true,
                estimatedCostMinPen = 120.0,
                estimatedCostMaxPen = 160.0,
                nextAvailableSlot = "Hoy, 4:15 PM",
                linkedIssue = null
            ),
            WorkshopItem(
                id = "w3",
                name = "Precision Motors & Diagnóstico OBD-II",
                rating = 4.9,
                reviewCount = 204,
                district = "Magdalena del Mar",
                distanceKm = 3.1,
                isCertifiedShiftIQ = true,
                hasFreeBaysToday = true,
                estimatedCostMinPen = 160.0,
                estimatedCostMaxPen = 220.0,
                nextAvailableSlot = "Hoy, 5:00 PM",
                linkedIssue = "Sensor O2 Banco 1"
            ),
            WorkshopItem(
                id = "w4",
                name = "Mecánica Automotriz San Miguel",
                rating = 4.5,
                reviewCount = 67,
                district = "San Miguel",
                distanceKm = 0.9,
                isCertifiedShiftIQ = false,
                hasFreeBaysToday = false,
                estimatedCostMinPen = 110.0,
                estimatedCostMaxPen = 150.0,
                nextAvailableSlot = "Mañana, 09:00 AM",
                linkedIssue = null
            )
        )

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

            _uiState.update { current ->
                current.copy(
                    allWorkshops = seedWorkshops,
                    filteredWorkshops = seedWorkshops,
                    selectedVehicle = vehicle
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            val updated = current.copy(searchQuery = query)
            updated.copy(filteredWorkshops = applyFilters(current.allWorkshops, query, current.selectedFilter))
        }
    }

    fun onFilterSelected(filter: WorkshopFilter) {
        _uiState.update { current ->
            val updated = current.copy(selectedFilter = filter)
            updated.copy(filteredWorkshops = applyFilters(current.allWorkshops, current.searchQuery, filter))
        }
    }

    private fun applyFilters(
        list: List<WorkshopItem>,
        query: String,
        filter: WorkshopFilter
    ): List<WorkshopItem> {
        return list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.district.contains(query, ignoreCase = true) ||
                    (item.linkedIssue?.contains(query, ignoreCase = true) == true)

            val matchesFilter = when (filter) {
                WorkshopFilter.ALL -> true
                WorkshopFilter.NEAR_ME -> item.distanceKm <= 2.0
                WorkshopFilter.RATING_45_PLUS -> item.rating >= 4.7
                WorkshopFilter.FREE_BAYS -> item.hasFreeBaysToday
            }

            matchesQuery && matchesFilter
        }
    }

    fun onSelectWorkshopForBooking(workshop: WorkshopItem) {
        _uiState.update { it.copy(selectedWorkshopForBooking = workshop, errorMessage = null) }
    }

    fun onDismissBookingDialog() {
        _uiState.update { it.copy(selectedWorkshopForBooking = null) }
    }

    fun onSelectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onSelectTimeSlot(slot: String) {
        _uiState.update { it.copy(selectedTimeSlot = slot) }
    }

    fun onBookingReasonChange(reason: String) {
        _uiState.update { it.copy(bookingReason = reason) }
    }

    fun confirmBooking() {
        val workshop = _uiState.value.selectedWorkshopForBooking ?: return
        val vehicle = _uiState.value.selectedVehicle ?: return
        val bookingState = _uiState.value

        _uiState.update { it.copy(isBookingLoading = true, errorMessage = null) }

        viewModelScope.launch {
            delay(450)
            // Demo local: no se crea una cita en el backend.
            val voucherCode = "SQ-APPT-${UUID.randomUUID().toString().take(6).uppercase()}"

            val voucher = BookingVoucher(
                appointmentCode = voucherCode,
                workshopName = workshop.name,
                district = workshop.district,
                scheduledDateTime = "${bookingState.selectedDate} • ${bookingState.selectedTimeSlot}",
                vehiclePlate = vehicle.plateNumber,
                bayNumber = "Bahía 2 (Elevador Hidráulico)",
                estimatedTotalPen = workshop.estimatedCostMinPen + 20.0
            )

            _uiState.update {
                it.copy(
                    isBookingLoading = false,
                    selectedWorkshopForBooking = null,
                    bookingSuccessVoucher = voucher
                )
            }
        }
    }

    fun onDismissVoucherDialog() {
        _uiState.update { it.copy(bookingSuccessVoucher = null) }
    }
}
