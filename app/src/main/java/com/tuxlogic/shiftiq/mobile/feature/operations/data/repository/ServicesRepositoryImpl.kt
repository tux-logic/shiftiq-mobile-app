package com.tuxlogic.shiftiq.mobile.feature.operations.data.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.operations.data.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.api.ServicesApiService
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.CreateServiceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.data.remote.dto.UpdateServiceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val servicesApiService: ServicesApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : ServicesRepository {

    override suspend fun getServices(branchId: BranchId?): AppResult<List<ServiceItem>> =
        withContext(dispatchers.io) {
            val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
            if (effectiveBranchId == null) {
                return@withContext AppResult.Failure(
                    AppError.Validation(
                        details = "branchId",
                        message = "No hay una sede activa seleccionada"
                    )
                )
            }
            safeApiCall {
                servicesApiService.getServices(effectiveBranchId.toString())
            }.map { dtos ->
                dtos.map { it.toDomain() }
            }
        }

    override suspend fun createService(
        branchId: BranchId,
        name: String,
        price: Double
    ): AppResult<ServiceItem> = withContext(dispatchers.io) {
        safeApiCall {
            servicesApiService.createService(
                CreateServiceRequestDto(
                    branchId = branchId.toString(),
                    name = name,
                    price = price
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun updateService(
        serviceId: ServiceId,
        name: String,
        price: Double
    ): AppResult<ServiceItem> = withContext(dispatchers.io) {
        safeApiCall {
            servicesApiService.updateService(
                serviceId = serviceId.toString(),
                request = UpdateServiceRequestDto(
                    name = name,
                    price = price
                )
            )
        }.map { it.toDomain() }
    }

    override suspend fun deleteService(serviceId: ServiceId): AppResult<Unit> =
        withContext(dispatchers.io) {
            safeApiCall {
                servicesApiService.deleteService(serviceId.toString())
            }
        }
}
