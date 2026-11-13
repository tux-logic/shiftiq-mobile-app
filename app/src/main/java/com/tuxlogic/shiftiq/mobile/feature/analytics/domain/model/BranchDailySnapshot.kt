package com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Money

data class BranchDailySnapshot(
    val id: AnalyticsSnapshotId,
    val branchId: BranchId,
    val snapshotDate: String,
    val totalRevenue: Money,
    val completedWorkOrdersCount: Int,
    val totalAppointmentsCount: Int,
    val lowStockAlertsCount: Int,
    val dtcAlertsCount: Int
)
