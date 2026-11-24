package com.tuxlogic.shiftiq.mobile.feature.fleet.application.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.core.application.usecase.GetCustomerByUserIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.core.domain.repository.CoreRepository
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.ActiveServiceCardData
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverDtcAlert
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverLiveTelemetry
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.DriverVehicleHealth
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcSeverity
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

data class DriverDashboardData(
    val driverName: String,
    val driverEmail: String = "",
    val driverRole: String = "",
    val selectedVehicle: B2CVehicleItem?,
    val availableVehicles: List<B2CVehicleItem>,
    val vehicleHealth: DriverVehicleHealth,
    val liveTelemetry: DriverLiveTelemetry,
    val activeService: ActiveServiceCardData?,
    val activeDtcAlert: DriverDtcAlert?,
    val isObdConnected: Boolean = false
)

class GetDriverDashboardUseCase @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val iotRepository: IotRepository,
    private val workOrderRepository: WorkOrderRepository,
    private val coreRepository: CoreRepository,
    private val getCustomerByUserId: GetCustomerByUserIdUseCase
) {
    suspend operator fun invoke(selectedVehicleId: String? = null): DriverDashboardData {
        val session = sessionDataStore.sessionState.firstOrNull()
        val userEmail = session?.userEmail.orEmpty()
        var driverName = ""
        var remoteVehicles: List<Vehicle> = emptyList()

        session?.userId?.let { userId ->
            when (val profileResult = getCustomerByUserId(userId.value.toString())) {
                is AppResult.Success -> {
                    val customer = profileResult.data
                    driverName = customer.displayName
                    remoteVehicles = when (val result = iotRepository.getCustomerVehicles(CustomerId(customer.id))) {
                        is AppResult.Success -> result.data
                        is AppResult.Failure -> emptyList()
                    }
                }

                is AppResult.Failure -> Unit
            }
        }

        if (driverName.isBlank() && userEmail.isNotBlank()) {
            driverName = userEmail.substringBefore("@")
                .split(".", "_", "-")
                .firstOrNull()
                .orEmpty()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        if (driverName.isBlank()) driverName = "Driver"

        val vehicles = remoteVehicles.map { it.toB2CVehicleItem() }
        val primaryVehicle = vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

        val activeService = primaryVehicle?.let { vehicle ->
            runCatching {
                val vehicleId = VehicleId(UUID.fromString(vehicle.id))
                val orders = workOrderRepository.getWorkOrders(vehicleId = vehicleId)
                val activeOrder = (orders as? AppResult.Success)?.data
                    ?.firstOrNull { it.status == WorkOrderStatus.IN_PROGRESS }
                activeOrder?.let { order ->
                    val branch = (coreRepository.getBranchById(order.branchId.value.toString()) as? AppResult.Success)?.data
                    val activeTask = order.tasks.firstOrNull { it.status == TaskStatus.DOING }
                    val completedTasks = order.tasks.count { it.status == TaskStatus.COMPLETED }
                    ActiveServiceCardData(
                        workOrderId = order.id.value.toString(),
                        workshopName = branch?.name,
                        phaseText = order.status.name,
                        mechanicName = activeTask?.assignedMechanicName,
                        currentJob = activeTask?.description?.takeIf(String::isNotBlank)
                            ?: order.diagnosticSummary.ifBlank { "No work details are available." },
                        remainingMinutes = null,
                        currentPhaseIndex = if (order.tasks.isEmpty()) null else (completedTasks + 1).coerceAtMost(order.tasks.size),
                        totalPhases = order.tasks.size.takeIf { it > 0 },
                        startedAt = order.createdAt
                    )
                }
            }.getOrNull()
        }

        val vehicleId = primaryVehicle?.id?.let { runCatching { VehicleId(UUID.fromString(it)) }.getOrNull() }
        val latestTelemetry = vehicleId?.let { id ->
            (iotRepository.getVehicleTelemetryHistory(id, page = 0, size = 1) as? AppResult.Success)
                ?.data
                ?.firstOrNull()
        }
        val dtcAlerts = vehicleId?.let { id ->
            (iotRepository.getVehicleDtcAlerts(id, page = 0, size = 5) as? AppResult.Success)
                ?.data
                .orEmpty()
        }.orEmpty()

        return DriverDashboardData(
            driverName = driverName,
            driverEmail = userEmail,
            driverRole = session?.userRole?.displayName.orEmpty(),
            selectedVehicle = primaryVehicle,
            availableVehicles = vehicles,
            vehicleHealth = buildHealth(latestTelemetry, dtcAlerts),
            liveTelemetry = latestTelemetry.toLiveTelemetry(),
            activeService = activeService,
            activeDtcAlert = dtcAlerts.firstOrNull()?.toDriverAlert(),
            isObdConnected = latestTelemetry != null
        )
    }

    private fun buildHealth(
        telemetry: TelemetrySnapshot?,
        alerts: List<DtcAlert>
    ): DriverVehicleHealth {
        if (telemetry == null && alerts.isEmpty()) {
            return DriverVehicleHealth(
                score = 0,
                title = "Vehicle health",
                description = "No OBD-II telemetry has been received for this vehicle yet.",
                hasCriticalAlerts = false
            )
        }
        val hasCritical = alerts.any {
            it.severity == DtcSeverity.HIGH || it.severity == DtcSeverity.CRITICAL
        }
        val score = when {
            hasCritical -> 55
            alerts.isNotEmpty() -> 72
            else -> 94
        }
        return DriverVehicleHealth(
            score = score,
            title = "Vehicle health",
            description = when {
                hasCritical -> "Critical DTC alerts require workshop attention."
                alerts.isNotEmpty() -> "Open DTC codes were reported by the linked OBD-II device."
                else -> "Latest telemetry looks stable."
            },
            hasCriticalAlerts = hasCritical
        )
    }

    private fun TelemetrySnapshot?.toLiveTelemetry(): DriverLiveTelemetry {
        if (this == null) {
            return DriverLiveTelemetry(isSynchronized = false)
        }
        return DriverLiveTelemetry(
            rpm = rpm,
            temperatureCelsius = temperature,
            batteryVoltage = 0.0,
            odometerKm = odometerKm.toInt(),
            fuelPercent = fuelLevelPercent,
            speedKmh = speedKmh,
            isSynchronized = true
        )
    }

    private fun DtcAlert.toDriverAlert(): DriverDtcAlert = DriverDtcAlert(
        code = dtcCode,
        title = "DTC $dtcCode",
        severity = severity.name.lowercase().replaceFirstChar { it.titlecase() },
        suggestedReviewKm = 0,
        description = description.ifBlank { "Diagnostic trouble code reported by the vehicle." }
    )

    private fun Vehicle.toB2CVehicleItem(): B2CVehicleItem = B2CVehicleItem(
        id = id.toString(),
        brand = brand,
        model = model,
        year = year,
        plateNumber = plateNumber.value,
        vin = vin.value,
        photoUrl = photoUrl,
        isMonitored = false,
        batteryVoltage = null,
        isPrimary = false
    )
}
