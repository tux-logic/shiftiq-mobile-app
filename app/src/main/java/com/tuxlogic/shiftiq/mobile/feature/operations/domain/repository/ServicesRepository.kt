package com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem

interface ServicesRepository {
    suspend fun getServices(branchId: BranchId? = null): AppResult<List<ServiceItem>>
    suspend fun createService(branchId: BranchId, name: String, price: Double): AppResult<ServiceItem>
    suspend fun updateService(serviceId: ServiceId, name: String, price: Double): AppResult<ServiceItem>
    suspend fun deleteService(serviceId: ServiceId): AppResult<Unit>
}
