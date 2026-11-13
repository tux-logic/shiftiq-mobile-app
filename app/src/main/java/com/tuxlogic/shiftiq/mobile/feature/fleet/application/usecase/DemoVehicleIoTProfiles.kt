package com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverVehicleHealth

data class DemoVehicleIoTProfile(
    val telemetry: DriverLiveTelemetry,
    val health: DriverVehicleHealth,
    val alert: DriverDtcAlert?
)

/** Fictional IoT values used only to exercise the customer app before an OBD-II device is available. */
object DemoVehicleIoTProfiles {
    private val profiles = listOf(
        DemoVehicleIoTProfile(
            telemetry = DriverLiveTelemetry(
                rpm = 837,
                temperatureCelsius = 88,
                batteryVoltage = 13.7,
                odometerKm = 48_120,
                fuelPercent = 68.0,
                isSynchronized = false
            ),
            health = DriverVehicleHealth(
                score = 94,
                description = "Datos IoT de demostración · perfil A",
                hasCriticalAlerts = false
            ),
            alert = DriverDtcAlert(
                code = "P0135",
                title = "Alerta de sensor de oxígeno",
                severity = "Media",
                suggestedReviewKm = 500,
                description = "Lectura IoT ficticia del circuito calefactor del sensor de oxígeno."
            )
        ),
        DemoVehicleIoTProfile(
            telemetry = DriverLiveTelemetry(
                rpm = 765,
                temperatureCelsius = 92,
                batteryVoltage = 12.4,
                odometerKm = 62_405,
                fuelPercent = 42.0,
                isSynchronized = false
            ),
            health = DriverVehicleHealth(
                score = 78,
                description = "Datos IoT de demostración · perfil B",
                hasCriticalAlerts = false
            ),
            alert = DriverDtcAlert(
                code = "P0420",
                title = "Eficiencia del catalizador baja",
                severity = "Media",
                suggestedReviewKm = 250,
                description = "Lectura IoT ficticia: la eficiencia del sistema catalítico está por debajo del umbral de demostración."
            )
        ),
        DemoVehicleIoTProfile(
            telemetry = DriverLiveTelemetry(
                rpm = 910,
                temperatureCelsius = 96,
                batteryVoltage = 13.9,
                odometerKm = 85_700,
                fuelPercent = 29.0,
                isSynchronized = false
            ),
            health = DriverVehicleHealth(
                score = 62,
                description = "Datos IoT de demostración · perfil C",
                hasCriticalAlerts = true
            ),
            alert = DriverDtcAlert(
                code = "P0301",
                title = "Falla de combustión en cilindro 1",
                severity = "Alta",
                suggestedReviewKm = 100,
                description = "Lectura IoT ficticia: se simula una falla de combustión para probar las alertas."
            )
        ),
        DemoVehicleIoTProfile(
            telemetry = DriverLiveTelemetry(
                rpm = 1_020,
                temperatureCelsius = 84,
                batteryVoltage = 14.1,
                odometerKm = 31_280,
                fuelPercent = 73.0,
                isSynchronized = false
            ),
            health = DriverVehicleHealth(
                score = 85,
                description = "Datos IoT de demostración · perfil D",
                hasCriticalAlerts = false
            ),
            alert = DriverDtcAlert(
                code = "P0171",
                title = "Mezcla de combustible demasiado pobre",
                severity = "Media",
                suggestedReviewKm = 300,
                description = "Lectura IoT ficticia: la mezcla simulada tiene más aire del esperado."
            )
        ),
        DemoVehicleIoTProfile(
            telemetry = DriverLiveTelemetry(
                rpm = 800,
                temperatureCelsius = 86,
                batteryVoltage = 12.8,
                odometerKm = 19_940,
                fuelPercent = 55.0,
                isSynchronized = false
            ),
            health = DriverVehicleHealth(
                score = 98,
                description = "Datos IoT de demostración · sin alertas",
                hasCriticalAlerts = false
            ),
            alert = null
        )
    )

    fun forVehicle(
        vehicle: B2CVehicleItem?,
        availableVehicles: List<B2CVehicleItem>
    ): DemoVehicleIoTProfile? {
        if (vehicle == null) return null

        val index = availableVehicles
            .sortedBy { it.id }
            .indexOfFirst { it.id == vehicle.id }
            .takeIf { it >= 0 }
            ?: Math.floorMod(vehicle.id.hashCode(), profiles.size)

        return profiles[index % profiles.size]
    }
}
