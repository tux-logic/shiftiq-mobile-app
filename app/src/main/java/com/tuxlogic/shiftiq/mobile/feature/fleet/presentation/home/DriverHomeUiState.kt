package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.ActiveServiceCardData
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverVehicleHealth

data class DriverHomeUiState(
    val isLoading: Boolean = false,
    val driverName: String = "Conductor",
    val driverEmail: String = "",
    val driverRole: String = "",
    val selectedVehicle: B2CVehicleItem? = null,
    val availableVehicles: List<B2CVehicleItem> = emptyList(),
    val vehicleHealth: DriverVehicleHealth = DriverVehicleHealth(),
    val liveTelemetry: DriverLiveTelemetry = DriverLiveTelemetry(),
    val activeService: ActiveServiceCardData? = null,
    val activeDtcAlert: DriverDtcAlert? = null,
    val isObdConnected: Boolean = false,
    val isVehicleSelectorOpen: Boolean = false,
    val isScanning: Boolean = false,
    val scanProgressMessage: String? = null,
    val errorMessage: String? = null
) {
    val avatarInitial: String
        get() = driverName.trim().firstOrNull()?.uppercase() ?: "C"
}
