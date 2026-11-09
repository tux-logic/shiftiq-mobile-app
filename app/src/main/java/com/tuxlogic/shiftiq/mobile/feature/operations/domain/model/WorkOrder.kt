package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId

data class WorkOrder(
    val id: WorkOrderId,
    val appointmentId: String,
    val branchId: BranchId,
    val vehicleId: VehicleId,
    val customerId: CustomerId,
    val internalNumber: Int,
    val formattedNumber: String,
    val status: WorkOrderStatus = WorkOrderStatus.PENDING,
    val diagnosticSummary: String,
    val mileageIn: Int,
    val totalAmount: Money,
    val tasks: List<WorkOrderTask> = emptyList(),
    val entryInspectionImages: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null
)
