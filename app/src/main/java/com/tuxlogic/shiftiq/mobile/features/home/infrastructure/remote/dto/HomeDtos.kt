package com.tuxlogic.shiftiq.mobile.features.home.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class TelemetrySnapshotDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("rpm") val rpm: Int? = null,
    @SerializedName("temperature") val temperature: Int? = null,
    @SerializedName("speedKmh") val speedKmh: Double? = null,
    @SerializedName("odometerKm") val odometerKm: Int? = null,
    @SerializedName("fuelLevelPercent") val fuelLevelPercent: Double? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class DtcAlertResponseDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("dtcCode") val dtcCode: String,
    @SerializedName("description") val description: String,
    @SerializedName("severity") val severity: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class WorkOrderResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("formattedNumber") val formattedNumber: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("diagnosticSummary") val diagnosticSummary: String? = null,
    @SerializedName("mileageIn") val mileageIn: Int? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)
