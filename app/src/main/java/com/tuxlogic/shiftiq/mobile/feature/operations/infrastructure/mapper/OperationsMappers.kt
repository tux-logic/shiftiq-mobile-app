package com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.EmployeeId
import com.tuxlogic.shiftiq.mobile.core.model.Money
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.ServiceResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.TaskProductResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.WorkOrderResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.infrastructure.remote.dto.WorkOrderTaskResourceDto
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ProductId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ReservedProduct
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.TaskStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderStatus
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderTask

fun ServiceResourceDto.toDomain(): ServiceItem = ServiceItem(
    id = ServiceId(id),
    branchId = BranchId(branchId),
    name = name,
    price = Money(price)
)

fun TaskProductResourceDto.toDomain(): ReservedProduct = ReservedProduct(
    productId = ProductId(productId),
    productName = productName ?: "Spare Part",
    quantity = quantity,
    unitPrice = Money(unitPrice),
    totalPrice = Money(totalPrice)
)

fun WorkOrderTaskResourceDto.toDomain(): WorkOrderTask = WorkOrderTask(
    id = TaskId(id),
    serviceId = ServiceId(serviceId),
    serviceName = serviceName,
    branchId = BranchId(branchId),
    assignedMechanicId = assignedMechanicId?.let { EmployeeId(it) },
    assignedMechanicName = assignedMechanicName,
    status = TaskStatus.fromString(status),
    description = description,
    price = Money(price),
    evidenceImages = evidenceImages ?: emptyList(),
    products = products?.map { it.toDomain() } ?: emptyList(),
    startedAt = startedAt,
    completedAt = completedAt
)

fun WorkOrderResourceDto.toDomain(): WorkOrder = WorkOrder(
    id = WorkOrderId(id),
    appointmentId = appointmentId,
    branchId = BranchId(branchId),
    vehicleId = VehicleId(vehicleId),
    customerId = CustomerId(customerId),
    internalNumber = internalNumber ?: 0,
    formattedNumber = formattedNumber ?: "",
    status = WorkOrderStatus.fromString(status),
    diagnosticSummary = diagnosticSummary,
    mileageIn = mileageIn,
    totalAmount = Money(totalAmount ?: 0.0),
    tasks = tasks?.map { it.toDomain() } ?: emptyList(),
    entryInspectionImages = entryInspectionImages ?: emptyList(),
    createdAt = createdAt ?: "",
    updatedAt = updatedAt ?: "",
    completedAt = completedAt
)
