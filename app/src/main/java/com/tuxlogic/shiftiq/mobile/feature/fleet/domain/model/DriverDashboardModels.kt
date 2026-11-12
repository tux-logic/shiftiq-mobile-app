package com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model

data class DriverVehicleHealth(
    val score: Int = 94,
    val title: String = "Salud General del Vehículo",
    val description: String = "Todos los sensores en rango óptimo • Sin alertas críticas",
    val hasCriticalAlerts: Boolean = false
)

data class DriverLiveTelemetry(
    val rpm: Int = 850,
    val temperatureCelsius: Int = 88,
    val batteryVoltage: Double = 13.8,
    val odometerKm: Int = 48120,
    val fuelPercent: Double = 68.0,
    val speedKmh: Double = 0.0,
    val isSynchronized: Boolean = true
)

data class ActiveServiceCardData(
    val workOrderId: String,
    val workshopName: String,
    val phaseText: String,
    val mechanicName: String,
    val currentJob: String,
    val remainingMinutes: Int,
    val currentPhaseIndex: Int = 3,
    val totalPhases: Int = 4,
    val startedAt: String = "08:30 AM"
)

data class DriverDtcAlert(
    val code: String = "P0135",
    val title: String = "Alerta Leve: Sensor de Oxígeno",
    val severity: String = "Media",
    val suggestedReviewKm: Int = 500,
    val description: String = "Lectura intermitente en circuito de precalentador del banco 1."
)

data class B2CVehicleItem(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val plateNumber: String,
    val vin: String,
    val photoUrl: String? = null,
    val isMonitored: Boolean = true,
    val batteryVoltage: Double = 13.8,
    val isPrimary: Boolean = true,
    val nextServiceKm: Int = 50000,
    val oilLifePercent: Int = 78
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
    val name: String,
    val rating: Double = 4.8,
    val reviewCount: Int = 115,
    val district: String = "San Miguel",
    val distanceKm: Double = 1.2,
    val isCertifiedShiftIQ: Boolean = true,
    val hasFreeBaysToday: Boolean = true,
    val estimatedCostMinPen: Double = 140.0,
    val estimatedCostMaxPen: Double = 180.0,
    val nextAvailableSlot: String = "Hoy, 3:30 PM",
    val linkedIssue: String? = "Sensor O2 Banco 1"
)
