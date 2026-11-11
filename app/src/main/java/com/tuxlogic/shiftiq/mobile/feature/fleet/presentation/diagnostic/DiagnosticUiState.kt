package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry

data class DiagnosticUiState(
    val isScanning: Boolean = false,
    val scanProgress: Float = 1.0f,
    val isObdConnected: Boolean = true,
    val dongleName: String = "OBD2-BT-9821",
    val signalStrengthDbm: Int = -42,
    val activeDtcAlert: DriverDtcAlert? = DriverDtcAlert(
        code = "P0135",
        title = "Alerta Leve: Sensor de Oxígeno",
        severity = "Media",
        suggestedReviewKm = 500,
        description = "Ligera variación en la pre-calefacción de la mezcla de combustible. Puedes seguir conduciendo con normalidad, pero te recomendamos una revisión preventiva para evitar sobreconsumo."
    ),
    val telemetry: DriverLiveTelemetry = DriverLiveTelemetry(
        rpm = 850,
        temperatureCelsius = 88,
        batteryVoltage = 13.8,
        odometerKm = 48120,
        fuelPercent = 68.0,
        speedKmh = 0.0,
        isSynchronized = true
    ),
    val engineLoadPercent: Int = 22,
    val intakePressureKpa: Int = 34,
    val fuelTrimPercent: Double = 1.6,
    val dtcResolvedMessage: String? = null
)
