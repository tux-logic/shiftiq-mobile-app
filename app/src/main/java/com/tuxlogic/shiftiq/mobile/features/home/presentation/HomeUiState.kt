package com.tuxlogic.shiftiq.mobile.features.home.presentation

import com.tuxlogic.shiftiq.mobile.features.home.domain.model.ActiveServiceInfo
import com.tuxlogic.shiftiq.mobile.features.home.domain.model.DriverVehicle
import com.tuxlogic.shiftiq.mobile.features.home.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.features.home.domain.model.TelemetryMetrics

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val customerName: String = "Rodrigo",
    val isDongleConnected: Boolean = true,
    val vehicle: DriverVehicle = DriverVehicle(
        id = "veh-primary-01",
        plateNumber = "ABC-123",
        brand = "Kia",
        model = "Rio",
        year = 2019,
        vin = "9BWCA41JX8P123456"
    ),
    val metrics: TelemetryMetrics = TelemetryMetrics(),
    val activeAlerts: List<DtcAlert> = emptyList(),
    val activeService: ActiveServiceInfo? = ActiveServiceInfo(),
    val errorMessage: String? = null
)
