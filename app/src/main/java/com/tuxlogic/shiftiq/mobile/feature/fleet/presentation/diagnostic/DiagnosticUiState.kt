package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry

data class EcuInspectionItem(
    val ecuId: String,
    val moduleName: String,
    val hasDtc: Boolean,
    val dtcCode: String? = null,
    val statusText: String
)

data class DiagnosticUiState(
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val isObdConnected: Boolean = false,
    val dongleName: String = "",
    val signalStrengthDbm: Int = 0,
    val activeDtcAlert: DriverDtcAlert? = null,
    val inspectedEcus: List<EcuInspectionItem> = emptyList(),
    val telemetry: DriverLiveTelemetry = DriverLiveTelemetry(isSynchronized = false),
    val engineLoadPercent: Int = 0,
    val intakePressureKpa: Int = 0,
    val fuelTrimPercent: Double = 0.0,
    val dtcResolvedMessage: String? = null
)
