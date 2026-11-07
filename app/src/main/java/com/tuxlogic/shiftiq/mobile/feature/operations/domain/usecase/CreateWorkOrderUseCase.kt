package com.tuxlogic.shiftiq.mobile.feature.operations.domain.usecase

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.repository.WorkOrderRepository
import javax.inject.Inject

class CreateWorkOrderUseCase @Inject constructor(
    private val workOrderRepository: WorkOrderRepository
) {
    suspend operator fun invoke(
        appointmentId: String,
        branchId: BranchId,
        vehicleId: VehicleId,
        customerId: CustomerId,
        diagnosticSummary: String,
        mileageIn: Int,
        entryInspectionImages: List<String> = emptyList()
    ): AppResult<WorkOrder> {
        if (appointmentId.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "appointmentId", message = "La cita asociada es requerida")
            )
        }
        val trimmedSummary = diagnosticSummary.trim()
        if (trimmedSummary.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "diagnosticSummary", message = "El diagnóstico inicial es requerido")
            )
        }
        if (mileageIn < 0) {
            return AppResult.Failure(
                AppError.Validation(details = "mileageIn", message = "El kilometraje de entrada no puede ser negativo")
            )
        }
        return workOrderRepository.createWorkOrder(
            appointmentId = appointmentId.trim(),
            branchId = branchId,
            vehicleId = vehicleId,
            customerId = customerId,
            diagnosticSummary = trimmedSummary,
            mileageIn = mileageIn,
            entryInspectionImages = entryInspectionImages
        )
    }
}
