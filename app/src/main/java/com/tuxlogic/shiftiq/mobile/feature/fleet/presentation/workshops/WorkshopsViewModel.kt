package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetBranchCatalogUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase.CreateAppointmentUseCase
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.WorkshopItem
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
class WorkshopsViewModel @Inject constructor(
    private val getBranchCatalog: GetBranchCatalogUseCase,
    private val sessionDataStore: SessionDataStore,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase,
    private val getCustomerVehicles: GetCustomerVehiclesUseCase,
    private val createAppointment: CreateAppointmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopsUiState())
    val uiState: StateFlow<WorkshopsUiState> = _uiState.asStateFlow()

    init {
        loadWorkshopsAndVehicle()
    }

    fun loadWorkshopsAndVehicle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val catalogResult = getBranchCatalog()
            val workshops = when (catalogResult) {
                is AppResult.Success -> catalogResult.data.map { item ->
                    WorkshopItem(
                        id = item.branchId,
                        workshopId = item.workshopId,
                        name = item.displayName,
                        address = item.address,
                        phone = item.phone,
                        district = item.address.substringAfterLast(",").trim().ifBlank { item.address },
                        isCertifiedShiftIQ = true,
                        latitude = item.latitude,
                        longitude = item.longitude
                    )
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = catalogResult.error.message)
                    }
                    return@launch
                }
            }

            val vehicle = loadPrimaryVehicle()

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    allWorkshops = workshops,
                    filteredWorkshops = applyFilters(workshops, current.searchQuery, current.selectedFilter),
                    selectedVehicle = vehicle,
                    errorMessage = if (workshops.isEmpty()) {
                        "No workshops are available to book yet."
                    } else null
                )
            }
        }
    }

    private suspend fun loadPrimaryVehicle(): B2CVehicleItem? {
        val userId = sessionDataStore.userId.firstOrNull()?.value?.toString() ?: return null
        val customer = (getCustomerByUserId(userId) as? AppResult.Success)?.data ?: return null
        val vehicles = (getCustomerVehicles(CustomerId(customer.id)) as? AppResult.Success)?.data.orEmpty()
        return vehicles.firstOrNull()?.let { vehicle ->
            B2CVehicleItem(
                id = vehicle.id.toString(),
                brand = vehicle.brand,
                model = vehicle.model,
                year = vehicle.year,
                plateNumber = vehicle.plateNumber.value,
                vin = vehicle.vin.value,
                photoUrl = vehicle.photoUrl,
                isMonitored = false,
                batteryVoltage = null,
                isPrimary = true
            )
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
                    item.address.contains(query, ignoreCase = true) ||
                    (item.linkedIssue?.contains(query, ignoreCase = true) == true)

            val matchesFilter = when (filter) {
                WorkshopFilter.ALL -> true
                WorkshopFilter.NEAR_ME -> true
                WorkshopFilter.RATING_45_PLUS -> item.rating >= 4.5 || item.rating == 0.0
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
        val vehicle = _uiState.value.selectedVehicle
        val bookingState = _uiState.value

        if (vehicle == null) {
            _uiState.update { it.copy(errorMessage = "Register a vehicle before booking an appointment.") }
            return
        }

        _uiState.update { it.copy(isBookingLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val userId = sessionDataStore.userId.firstOrNull()?.value?.toString()
            if (userId.isNullOrBlank()) {
                _uiState.update {
                    it.copy(isBookingLoading = false, errorMessage = "Your session expired. Please sign in again.")
                }
                return@launch
            }

            val customer = when (val result = getCustomerByUserId(userId)) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isBookingLoading = false, errorMessage = result.error.message) }
                    return@launch
                }
            }

            val scheduledStart = resolveScheduledStart(bookingState.selectedDate, bookingState.selectedTimeSlot)
            if (scheduledStart == null) {
                _uiState.update {
                    it.copy(
                        isBookingLoading = false,
                        errorMessage = "Select a valid date and time slot for the appointment."
                    )
                }
                return@launch
            }

            when (
                val result = createAppointment(
                    branchId = UUID.fromString(workshop.id),
                    customerId = UUID.fromString(customer.id),
                    vehicleId = UUID.fromString(vehicle.id),
                    scheduledStart = scheduledStart,
                    notes = bookingState.bookingReason.ifBlank { null }
                )
            ) {
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isBookingLoading = false, errorMessage = result.error.message)
                    }
                }

                is AppResult.Success -> {
                    val appointment = result.data
                    val voucher = BookingVoucher(
                        appointmentCode = appointment.id.toString().take(8).uppercase(),
                        workshopName = workshop.name,
                        district = workshop.district,
                        scheduledDateTime = "${bookingState.selectedDate} • ${bookingState.selectedTimeSlot}",
                        vehiclePlate = vehicle.plateNumber,
                        bayNumber = workshop.branchNameOrFallback(),
                        estimatedTotalPen = workshop.estimatedCostMinPen
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
        }
    }

    private fun WorkshopItem.branchNameOrFallback(): String =
        name.substringAfter(" · ", missingDelimiterValue = name)

    private fun resolveScheduledStart(dateLabel: String, timeLabel: String): String? {
        if (dateLabel.isBlank() || timeLabel.isBlank()) return null
        return runCatching {
            val today = LocalDate.now()
            val date = when {
                dateLabel.contains("Hoy", ignoreCase = true) || dateLabel.contains("Today", ignoreCase = true) -> today
                dateLabel.contains("Mañana", ignoreCase = true) || dateLabel.contains("Tomorrow", ignoreCase = true) -> today.plusDays(1)
                else -> {
                    val parser = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "PE"))
                    LocalDate.parse(dateLabel.trim(), parser)
                }
            }
            val time = LocalTime.parse(
                timeLabel.trim().uppercase(Locale.US),
                DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            )
            LocalDateTime.of(date, time).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }.getOrNull()
    }

    fun onDismissVoucherDialog() {
        _uiState.update { it.copy(bookingSuccessVoucher = null) }
    }
}
