package com.tuxlogic.shiftiq.mobile.features.home.domain.model

enum class AlertSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class DtcAlert(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val severity: AlertSeverity = AlertSeverity.MEDIUM,
    val suggestedReviewKm: Int = 500
) {
    val severityLabel: String
        get() = when (severity) {
            AlertSeverity.LOW -> "Severidad: Baja"
            AlertSeverity.MEDIUM -> "Severidad: Media"
            AlertSeverity.HIGH -> "Severidad: Alta"
            AlertSeverity.CRITICAL -> "Severidad: Crítica"
        }
}
