package com.tuxlogic.shiftiq.mobile.features.home.domain.model

data class TelemetryMetrics(
    val healthScore: Int = 94,
    val rpm: Int = 850,
    val coolantTempCelsius: Int = 88,
    val batteryVoltage: Double = 13.8,
    val odometerKm: Int = 48120,
    val fuelLevelPercent: Double = 68.0,
    val speedKmh: Double = 0.0
) {
    val isBatteryOptimal: Boolean
        get() = batteryVoltage in 12.4..14.5

    val isCoolantNormal: Boolean
        get() = coolantTempCelsius in 75..95

    val formattedOdometer: String
        get() = "%,d km".format(odometerKm).replace(',', ' ')
}
