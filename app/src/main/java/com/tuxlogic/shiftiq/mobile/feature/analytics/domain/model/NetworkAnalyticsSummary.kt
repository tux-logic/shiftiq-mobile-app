package com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

data class NetworkAnalyticsSummary(
    val totalActiveBranchesCount: Int,
    val totalNetworkRevenue: Money,
    val totalNetworkCompletedWorkOrders: Int,
    val totalNetworkAppointments: Int,
    val totalNetworkDtcAlerts: Int
)
