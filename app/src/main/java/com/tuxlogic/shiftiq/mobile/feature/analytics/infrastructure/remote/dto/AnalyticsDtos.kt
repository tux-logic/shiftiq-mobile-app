package com.tuxlogic.shiftiq.mobile.feature.analytics.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName

data class BranchDailySummaryResourceDto(
    @SerializedName("branchId") val branchId: String,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("completedWorkOrdersCount") val completedWorkOrdersCount: Int,
    @SerializedName("totalAppointmentsCount") val totalAppointmentsCount: Int,
    @SerializedName("lowStockAlertsCount") val lowStockAlertsCount: Int,
    @SerializedName("dtcAlertsCount") val dtcAlertsCount: Int
)

data class BranchDailySnapshotResourceDto(
    @SerializedName("id") val id: String,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("snapshotDate") val snapshotDate: String,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("completedWorkOrdersCount") val completedWorkOrdersCount: Int,
    @SerializedName("totalAppointmentsCount") val totalAppointmentsCount: Int,
    @SerializedName("lowStockAlertsCount") val lowStockAlertsCount: Int,
    @SerializedName("dtcAlertsCount") val dtcAlertsCount: Int
)

data class NetworkSummaryResourceDto(
    @SerializedName("totalActiveBranchesCount") val totalActiveBranchesCount: Int,
    @SerializedName("totalNetworkRevenue") val totalNetworkRevenue: Double,
    @SerializedName("totalNetworkCompletedWorkOrders") val totalNetworkCompletedWorkOrders: Int,
    @SerializedName("totalNetworkAppointments") val totalNetworkAppointments: Int,
    @SerializedName("totalNetworkDtcAlerts") val totalNetworkDtcAlerts: Int
)
