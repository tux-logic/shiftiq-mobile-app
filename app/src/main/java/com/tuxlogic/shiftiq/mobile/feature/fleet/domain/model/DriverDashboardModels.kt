package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model

data class DriverVehicleHealth(
    val score: Int = 0,
    val title: String = "Vehicle health",
    val description: String = "Waiting for OBD-II telemetry",
    val hasCriticalAlerts: Boolean = false
)

data class DriverLiveTelemetry(
    val rpm: Int = 0,
    val temperatureCelsius: Int = 0,
    val batteryVoltage: Double = 0.0,
    val odometerKm: Int = 0,
    val fuelPercent: Double = 0.0,
    val speedKmh: Double = 0.0,
    val isSynchronized: Boolean = false
)

data class ActiveServiceCardData(
    val workOrderId: String,
    val workshopName: String? = null,
    val phaseText: String,
    val mechanicName: String? = null,
    val currentJob: String,
    val remainingMinutes: Int? = null,
    val currentPhaseIndex: Int? = null,
    val totalPhases: Int? = null,
    val startedAt: String? = null
)

data class DriverDtcAlert(
    val code: String = "",
    val title: String = "",
    val severity: String = "",
    val suggestedReviewKm: Int = 0,
    val description: String = ""
)

data class B2CVehicleItem(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val plateNumber: String,
    val vin: String,
    val photoUrl: String? = null,
    val isMonitored: Boolean = false,
    val batteryVoltage: Double? = null,
    val isPrimary: Boolean = false,
    val nextServiceKm: Int? = null,
    val oilLifePercent: Int? = null
)

data class MaintenanceHistoryItem(
    val id: String,
    val date: String,
    val title: String,
    val mileage: Int,
    val workshopName: String,
    val description: String,
    val totalCostPen: Double,
    val voucherUrl: String? = null,
    val voucherId: String? = null,
    val hasPdfVoucher: Boolean = false,
    val hasTechnicalDetail: Boolean = false
)

data class WorkshopItem(
    val id: String,
    val workshopId: String = "",
    val name: String,
    val address: String = "",
    val phone: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val district: String = "",
    val distanceKm: Double = 0.0,
    val isCertifiedShiftIQ: Boolean = true,
    val hasFreeBaysToday: Boolean = true,
    val estimatedCostMinPen: Double = 0.0,
    val estimatedCostMaxPen: Double = 0.0,
    val nextAvailableSlot: String = "",
    val linkedIssue: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
