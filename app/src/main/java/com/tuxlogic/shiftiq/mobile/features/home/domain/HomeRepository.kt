package com.tuxlogic.shiftiq.mobile.features.home.domain

import com.tuxlogic.shiftiq.mobile.features.home.domain.model.HomeDashboardData

interface HomeRepository {
    suspend fun getHomeDashboardData(forceRefresh: Boolean = false): Result<HomeDashboardData>
}
