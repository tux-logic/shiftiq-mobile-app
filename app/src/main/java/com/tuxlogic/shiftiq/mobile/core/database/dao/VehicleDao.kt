package com.tuxlogic.shiftiq.mobile.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.tuxlogic.shiftiq.mobile.core.database.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Query("SELECT * FROM vehicles ORDER BY isPrimary DESC, brand ASC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    suspend fun getVehicleById(id: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE isPrimary = 1 LIMIT 1")
    fun getPrimaryVehicle(): Flow<VehicleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicleById(id: String)

    @Query("DELETE FROM vehicles")
    suspend fun clearAll()
}
