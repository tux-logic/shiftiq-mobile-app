package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

import com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model.ProductItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.ServiceItem
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher

data class WorkOrderMechanicOption(val id: String, val name: String)

data class LiveWorkOrderUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val workOrder: WorkOrder? = null,
    val vehicleDescription: String = "",
    val customerName: String = "",
    val customerDocumentType: String = "DNI",
    val customerDocumentNumber: String = "",
    val emittedVoucher: Voucher? = null,
    val currentEmployeeIds: Set<String> = emptySet(),
    val employeeIdentityLoaded: Boolean = false,
    val availableServices: List<ServiceItem> = emptyList(),
    val availableProducts: List<ProductItem> = emptyList(),
    val availableMechanics: List<WorkOrderMechanicOption> = emptyList()
)
