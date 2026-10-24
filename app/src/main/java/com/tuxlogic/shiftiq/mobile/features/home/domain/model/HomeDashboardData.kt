package com.tuxlogic.shiftiq.mobile.features.home.domain.model

data class ActiveServiceInfo(
    val workshopName: String = "Taller Silva",
    val statusText: String = "Fase 3 de 4: En Rampa (Bahía 2) • Téc. Roberto Silva",
    val taskDescription: String = "Trabajo en ejecución: Reemplazo de Sensor O2 Bosch (45 min restantes)",
    val startedAt: String = "08:30 AM",
    val completedSteps: Int = 3,
    val totalSteps: Int = 4
)

data class HomeDashboardData(
    val customerName: String,
    val isDongleConnected: Boolean,
    val vehicle: DriverVehicle,
    val metrics: TelemetryMetrics,
    val activeAlerts: List<DtcAlert>,
    val activeService: ActiveServiceInfo? = null,
    val isOffline: Boolean = false
)
