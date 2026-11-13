package com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder

data class LiveWorkOrderTaskItem(
    val title: String,
    val status: String,
    val price: String,
    val mechanicName: String?,
    val evidenceCount: Int
)

data class LiveWorkOrderUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val orderNumber: String = "",
    val vehicleDescription: String = "",
    val status: String = "",
    val diagnosticSummary: String = "",
    val mileageIn: Int? = null,
    val totalAmount: String? = null,
    val tasks: List<LiveWorkOrderTaskItem> = emptyList()
)
