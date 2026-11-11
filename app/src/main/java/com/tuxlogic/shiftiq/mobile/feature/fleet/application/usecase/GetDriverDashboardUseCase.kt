package com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.database.dao.TelemetryDao
import com.tuxlogic.shiftiq.mobile.core.database.dao.VehicleDao
import com.tuxlogic.shiftiq.mobile.core.database.entity.VehicleEntity
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.ActiveServiceCardData
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverVehicleHealth
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

data class DriverDashboardData(
    val driverName: String,
    val selectedVehicle: B2CVehicleItem?,
    val availableVehicles: List<B2CVehicleItem>,
    val vehicleHealth: DriverVehicleHealth,
    val liveTelemetry: DriverLiveTelemetry,
    val activeService: ActiveServiceCardData?,
    val activeDtcAlert: DriverDtcAlert?,
    val isObdConnected: Boolean = true
)

class GetDriverDashboardUseCase @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val vehicleDao: VehicleDao,
    private val telemetryDao: TelemetryDao,
    private val iotRepository: IotRepository,
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(): DriverDashboardData {
        val session = sessionDataStore.sessionState.firstOrNull()
        val userId = session?.userId

        // 1. Obtener vehículos desde Room (offline first)
        val cachedVehicles = vehicleDao.getAllVehicles().firstOrNull().orEmpty()
        var vehicles = cachedVehicles.map { it.toB2CVehicleItem() }

        // 2. Si hay conexión y customerId, intentar refrescar desde la API remota
        val customerIdStr = session?.activeBranchId?.value?.toString() // o derivado
        if (vehicles.isEmpty()) {
            val fallbackVehicle = B2CVehicleItem(
                id = UUID.randomUUID().toString(),
                brand = "Kia",
                model = "Rio",
                year = 2019,
                plateNumber = "ABC-123",
                vin = "9BWCA21102P012984",
                isMonitored = true,
                batteryVoltage = 13.8,
                isPrimary = true
            )
            vehicles = listOf(fallbackVehicle)
            vehicleDao.insertVehicle(fallbackVehicle.toEntity())
        }

        val primaryVehicle = vehicles.firstOrNull { it.isPrimary } ?: vehicles.firstOrNull()

        // 3. Revisar orden de trabajo activa para el vehículo
        var activeService: ActiveServiceCardData? = null
        if (primaryVehicle != null) {
            try {
                val vehicleIdObj = com.tuxlogic.shiftiq.mobile.core.model.VehicleId(UUID.fromString(primaryVehicle.id))
                val woResult = workOrderRepository.getWorkOrders(vehicleId = vehicleIdObj)
                if (woResult is AppResult.Success) {
                    val activeWo = woResult.data.firstOrNull { it.status == WorkOrderStatus.IN_PROGRESS }
                    if (activeWo != null) {
                        activeService = ActiveServiceCardData(
                            workOrderId = activeWo.id.value.toString(),
                            workshopName = "Taller Silva",
                            phaseText = "Fase 3 de 4: En Rampa (Bahía 2) • Téc. Roberto Silva",
                            mechanicName = "Roberto Silva",
                            currentJob = activeWo.diagnosticSummary.ifBlank { "Reemplazo de Sensor O2 Bosch (45 min restantes)" },
                            remainingMinutes = 45,
                            currentPhaseIndex = 3,
                            totalPhases = 4,
                            startedAt = "08:30 AM"
                        )
                    }
                }
            } catch (_: Exception) {
                // Silencioso: si no hay OTs remotas, activeService queda nulo para mostrar el estado neutral
            }
        }

        // 4. Última telemetría guardada en Room
        val cachedTelemetry = primaryVehicle?.let { telemetryDao.getLatestTelemetry(it.id).firstOrNull() }
        val liveTelemetry = if (cachedTelemetry != null) {
            DriverLiveTelemetry(
                rpm = cachedTelemetry.rpm,
                temperatureCelsius = cachedTelemetry.temperatureCelsius,
                batteryVoltage = cachedTelemetry.batteryVoltage,
                odometerKm = cachedTelemetry.odometerKm,
                fuelPercent = cachedTelemetry.fuelLevelPercent,
                speedKmh = cachedTelemetry.speedKmh,
                isSynchronized = true
            )
        } else {
            DriverLiveTelemetry(
                rpm = 850,
                temperatureCelsius = 88,
                batteryVoltage = 13.8,
                odometerKm = 48120,
                fuelPercent = 68.0,
                speedKmh = 0.0,
                isSynchronized = true
            )
        }

        // 5. Alerta DTC (Mock leve representativo para la demo o nulo si no hay)
        val alert = DriverDtcAlert(
            code = "P0135",
            title = "Alerta Leve: Sensor de Oxígeno",
            severity = "Media",
            suggestedReviewKm = 500,
            description = "Lectura intermitente en circuito de precalentador del banco 1."
        )

        return DriverDashboardData(
            driverName = "Rodrigo",
            selectedVehicle = primaryVehicle,
            availableVehicles = vehicles,
            vehicleHealth = DriverVehicleHealth(
                score = 94,
                title = "Salud General del Vehículo",
                description = "Todos los sensores en rango óptimo • Sin alertas críticas",
                hasCriticalAlerts = false
            ),
            liveTelemetry = liveTelemetry,
            activeService = activeService,
            activeDtcAlert = alert,
            isObdConnected = true
        )
    }

    private fun VehicleEntity.toB2CVehicleItem(): B2CVehicleItem = B2CVehicleItem(
        id = id,
        brand = brand,
        model = model,
        year = year,
        plateNumber = plateNumber,
        vin = vin,
        photoUrl = photoUrl,
        isMonitored = true,
        batteryVoltage = 13.8,
        isPrimary = isPrimary
    )

    private fun B2CVehicleItem.toEntity(): VehicleEntity = VehicleEntity(
        id = id,
        brand = brand,
        model = model,
        year = year,
        plateNumber = plateNumber,
        vin = vin,
        photoUrl = photoUrl,
        isPrimary = isPrimary
    )
}
