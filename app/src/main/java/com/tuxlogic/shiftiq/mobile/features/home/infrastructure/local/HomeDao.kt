package com.tuxlogic.shiftiq.mobile.features.home.infrastructure.local

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface HomeDao {

    @Query("SELECT * FROM home_dashboard WHERE id = 'primary' LIMIT 1")
    suspend fun getCachedDashboard(): HomeDashboardEntity?

    @Upsert
    suspend fun saveDashboard(dashboard: HomeDashboardEntity)

    @Query("DELETE FROM home_dashboard")
    suspend fun clearDashboard()
}
