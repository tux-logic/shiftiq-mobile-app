package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem

data class GarageUiState(
    val isLoading: Boolean = false,
    val vehicles: List<B2CVehicleItem> = emptyList(),
    val currentOdometerKm: Int = 48120,
    val nextServiceKm: Int = 50000,
    val batteryHealthVoltage: Double = 13.8,
    val engineOilLifePercent: Int = 78,
    val maintenanceHistory: List<MaintenanceHistoryItem> = emptyList(),
    val errorMessage: String? = null
)
