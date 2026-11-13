package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

data class CalibrationValueItem(
    val title: String,
    val initialValue: String,
    val finalValue: String,
    val statusText: String
)

data class ProtocolOperationItem(
    val title: String,
    val description: String
)

data class UsedSupplyItem(
    val name: String,
    val description: String,
    val badge: String = "INCLUIDO"
)

data class TechnicalInterventionDetailUiState(
    val expNumber: String = "",
    val isSealed: Boolean = false,
    val statusText: String = "",
    val odometerKm: Int = 0,
    val serviceTitle: String = "",
    val executionDate: String = "",
    val workshopName: String = "",
    val workshopAddress: String = "",
    val technicianName: String = "",
    val technicianCert: String = "",
    val totalCostPen: Double? = null,
    val paymentMethod: String = "",
    val laserCalibrations: List<CalibrationValueItem> = emptyList(),
    val protocolOperations: List<ProtocolOperationItem> = emptyList(),
    val usedSupplies: List<UsedSupplyItem> = emptyList(),
    val technicianObservation: String = "",
    val warrantyPolicy: String = "",
    val voucherId: String = "",
    val isLoading: Boolean = true,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)
