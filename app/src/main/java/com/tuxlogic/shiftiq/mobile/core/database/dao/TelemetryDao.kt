package com.tuxlogic.shiftiq.mobile.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.tuxlogic.shiftiq.mobile.core.database.entity.TelemetrySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {

    @Query("SELECT * FROM telemetry_snapshots WHERE vehicleId = :vehicleId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestTelemetry(vehicleId: String): Flow<TelemetrySnapshotEntity?>

    @Query("SELECT * FROM telemetry_snapshots WHERE vehicleId = :vehicleId ORDER BY timestamp DESC LIMIT :limit")
    fun getTelemetryHistory(vehicleId: String, limit: Int = 50): Flow<List<TelemetrySnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(snapshot: TelemetrySnapshotEntity)

    @Query("DELETE FROM telemetry_snapshots WHERE vehicleId = :vehicleId")
    suspend fun clearVehicleTelemetry(vehicleId: String)
}
