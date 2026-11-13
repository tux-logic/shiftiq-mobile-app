package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem

data class GarageUiState(
    val isLoading: Boolean = false,
    val vehicles: List<B2CVehicleItem> = emptyList(),
    val currentOdometerKm: Int? = null,
    val nextServiceKm: Int? = null,
    val batteryHealthVoltage: Double? = null,
    val engineOilLifePercent: Int? = null,
    val maintenanceHistory: List<MaintenanceHistoryItem> = emptyList(),
    val errorMessage: String? = null,
    val historyErrorMessage: String? = null
)
