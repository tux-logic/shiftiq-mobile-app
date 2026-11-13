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
    suspend operator fun invoke(): DriverDashboardData {
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
        if (driverName.isBlank()) driverName = "Conductor"

        val vehicles = remoteVehicles.map { it.toB2CVehicleItem() }
        val primaryVehicle = vehicles.firstOrNull()

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

        // Every vehicle gets its own clearly identified demo profile until a real OBD-II device is available.
        val demoProfile = DemoVehicleIoTProfiles.forVehicle(primaryVehicle, vehicles)

        return DriverDashboardData(
            driverName = driverName,
            driverEmail = userEmail,
            driverRole = session?.userRole?.displayName.orEmpty(),
            selectedVehicle = primaryVehicle,
            availableVehicles = vehicles,
            vehicleHealth = demoProfile?.health ?: DriverVehicleHealth(
                description = "Datos IoT de demostración · agrega un vehículo para ver un perfil.",
                hasCriticalAlerts = false
            ),
            liveTelemetry = demoProfile?.telemetry ?: DriverLiveTelemetry(isSynchronized = false),
            activeService = activeService,
            activeDtcAlert = demoProfile?.alert,
            isObdConnected = false
        )
    }

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
