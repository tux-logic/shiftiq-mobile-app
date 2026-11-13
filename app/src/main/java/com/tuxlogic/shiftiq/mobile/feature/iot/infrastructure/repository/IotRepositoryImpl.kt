package com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.mapper.toDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.api.IotApiService
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.LinkDeviceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.RegisterDeviceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.RegisterVehicleRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.UpdateDeviceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.UpdateRegistrationStatusRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.infrastructure.remote.dto.UpdateVehicleRequestDto
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DeviceRegistration
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.DtcAlert
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.MacAddress
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Obd2Device
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.RegistrationStatus
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetryBatch
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.TelemetrySnapshot
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IotRepositoryImpl @Inject constructor(
    private val iotApiService: IotApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : IotRepository {

    override suspend fun registerVehicle(
        plateNumber: PlateNumber,
        brand: String,
        model: String,
        year: Int,
        vin: Vin,
        photoUrl: String?,
        customerId: CustomerId?
    ): AppResult<Vehicle> = withContext(dispatchers.io) {
        val request = RegisterVehicleRequestDto(
            plateNumber = plateNumber.value,
            brand = brand,
            model = model,
            year = year,
            vin = vin.value,
            photoUrl = photoUrl,
            customerId = customerId?.toString()
        )
        when (val response = safeApiCall {
            iotApiService.registerVehicle(request)
        }) {
            is AppResult.Failure -> response
            is AppResult.Success -> {
                val registeredVehicleId = response.data.vehicleId
                    ?.takeIf(String::isNotBlank)
                    ?: response.data.id?.takeIf(String::isNotBlank)

                if (registeredVehicleId == null) {
                    AppResult.Failure(
                        AppError.Server(
                            message = "El servidor no devolvió el identificador del vehículo.",
                            details = "POST /api/v1/vehicles response did not include vehicleId or id"
                        )
                    )
                } else {
                    AppResult.Success(
                        Vehicle(
                            id = VehicleId(registeredVehicleId),
                            plateNumber = plateNumber,
                            brand = brand,
                            model = model,
                            year = year,
                            vin = vin,
                            photoUrl = photoUrl,
                            customerId = customerId
                        )
                    )
                }
            }
        }
    }

    override suspend fun getVehicleById(id: VehicleId): AppResult<Vehicle> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.getVehicleById(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun getVehiclesByBranch(branchId: BranchId?): AppResult<List<Vehicle>> =
        withContext(dispatchers.io) {
            val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
            if (effectiveBranchId == null) {
                return@withContext AppResult.Failure(
                    AppError.Validation(details = "branchId", message = "No active branch selected")
                )
            }
            safeApiCall {
                iotApiService.getVehicles(branchId = effectiveBranchId.toString())
            }.map { dtos ->
                dtos.map { it.toDomain() }
            }
        }

    override suspend fun getCustomerVehicles(customerId: CustomerId): AppResult<List<Vehicle>> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.getCustomerVehicles(customerId = customerId.toString())
            }.map { dtos ->
                dtos.map { it.toDomain() }
            }
        }

    override suspend fun updateVehicle(
        id: VehicleId,
        plateNumber: PlateNumber,
        brand: String,
        model: String,
        year: Int,
        vin: Vin,
        photoUrl: String?
    ): AppResult<Vehicle> = withContext(dispatchers.io) {
        val request = UpdateVehicleRequestDto(
            plateNumber = plateNumber.value,
            brand = brand,
            model = model,
            year = year,
            vin = vin.value,
            photoUrl = photoUrl
        )
        safeApiCall {
            iotApiService.updateVehicle(id.toString(), request)
        }.map { it.toDomain() }
    }

    override suspend fun deleteVehicle(id: VehicleId): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.deleteVehicle(id.toString())
            }
        }

    override suspend fun registerDevice(
        branchId: BranchId,
        macAddress: MacAddress
    ): AppResult<Obd2Device> = withContext(dispatchers.io) {
        val request = RegisterDeviceRequestDto(
            branchId = branchId.toString(),
            macAddress = macAddress.value
        )
        safeApiCall {
            iotApiService.registerDevice(request)
        }.map { it.toDomain() }
    }

    override suspend fun getDevices(
        branchId: BranchId?,
        status: String?
    ): AppResult<List<Obd2Device>> = withContext(dispatchers.io) {
        val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
        if (effectiveBranchId == null) {
            return@withContext AppResult.Failure(
                AppError.Validation(details = "branchId", message = "No active branch selected")
            )
        }
        safeApiCall {
            iotApiService.getDevices(branchId = effectiveBranchId.toString(), status = status)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getDeviceById(id: DeviceId): AppResult<Obd2Device> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.getDeviceById(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun updateDevice(
        id: DeviceId,
        macAddress: MacAddress
    ): AppResult<Obd2Device> = withContext(dispatchers.io) {
        val request = UpdateDeviceRequestDto(
            macAddress = macAddress.value
        )
        safeApiCall {
            iotApiService.updateDevice(id.toString(), request)
        }.map { it.toDomain() }
    }

    override suspend fun deleteDevice(id: DeviceId): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.deleteDevice(id.toString())
            }
        }

    override suspend fun linkDeviceToVehicle(
        obd2DeviceId: DeviceId,
        branchId: BranchId,
        vehicleId: VehicleId
    ): AppResult<DeviceRegistration> = withContext(dispatchers.io) {
        val request = LinkDeviceRequestDto(
            obd2DeviceId = obd2DeviceId.toString(),
            branchId = branchId.toString(),
            vehicleId = vehicleId.toString()
        )
        safeApiCall {
            iotApiService.linkDevice(request)
        }.map { it.toDomain() }
    }

    override suspend fun unlinkDeviceFromVehicle(
        registrationId: RegistrationId
    ): AppResult<DeviceRegistration> = withContext(dispatchers.io) {
        val request = UpdateRegistrationStatusRequestDto(
            status = "INACTIVE"
        )
        safeApiCall {
            iotApiService.updateRegistrationStatus(registrationId.toString(), request)
        }.map { it.toDomain() }
    }

    override suspend fun getRegistrations(
        branchId: BranchId?,
        status: RegistrationStatus
    ): AppResult<List<DeviceRegistration>> = withContext(dispatchers.io) {
        val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
        if (effectiveBranchId == null) {
            return@withContext AppResult.Failure(
                AppError.Validation(details = "branchId", message = "No active branch selected")
            )
        }
        safeApiCall {
            iotApiService.getRegistrations(branchId = effectiveBranchId.toString(), status = status.name)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getLatestTelemetrySnapshot(deviceId: DeviceId): AppResult<TelemetrySnapshot> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.getLatestTelemetrySnapshot(deviceId.toString())
            }.map { it.toDomain() }
        }

    override suspend fun getDeviceTelemetryHistory(
        deviceId: DeviceId,
        page: Int,
        size: Int
    ): AppResult<List<TelemetrySnapshot>> = withContext(dispatchers.io) {
        safeApiCall {
            iotApiService.getDeviceTelemetrySnapshots(deviceId.toString(), page, size)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getVehicleTelemetryHistory(
        vehicleId: VehicleId,
        page: Int,
        size: Int
    ): AppResult<List<TelemetrySnapshot>> = withContext(dispatchers.io) {
        safeApiCall {
            iotApiService.getVehicleTelemetrySnapshots(vehicleId.toString(), page, size)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getRegistrationTelemetryHistory(
        registrationId: RegistrationId,
        page: Int,
        size: Int
    ): AppResult<List<TelemetrySnapshot>> = withContext(dispatchers.io) {
        safeApiCall {
            iotApiService.getRegistrationTelemetrySnapshots(registrationId.toString(), page, size)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getVehicleDtcAlerts(
        vehicleId: VehicleId,
        page: Int,
        size: Int
    ): AppResult<List<DtcAlert>> = withContext(dispatchers.io) {
        safeApiCall {
            iotApiService.getVehicleDtcAlerts(vehicleId.toString(), page, size)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getRegistrationDtcAlerts(
        registrationId: RegistrationId,
        page: Int,
        size: Int
    ): AppResult<List<DtcAlert>> = withContext(dispatchers.io) {
        safeApiCall {
            iotApiService.getRegistrationDtcAlerts(registrationId.toString(), page, size)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun ingestTelemetryBatch(batch: TelemetryBatch): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                iotApiService.ingestTelemetryBatch(batch.toDto())
            }.map { }
        }
}
