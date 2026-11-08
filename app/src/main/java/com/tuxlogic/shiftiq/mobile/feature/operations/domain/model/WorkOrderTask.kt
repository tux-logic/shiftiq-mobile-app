package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.core.model.Money

data class WorkOrderTask(
    val id: TaskId,
    val serviceId: ServiceId,
    val serviceName: String? = null,
    val branchId: BranchId,
    val assignedMechanicId: EmployeeId? = null,
    val assignedMechanicName: String? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val description: String,
    val price: Money,
    val evidenceImages: List<String> = emptyList(),
    val products: List<ReservedProduct> = emptyList(),
    val startedAt: String? = null,
    val completedAt: String? = null
)
