package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.external

data class ExternalServiceRegistrationUiState(
    val serviceType: String = "",
    val availableServiceTypes: List<String> = listOf(
        "Cambio de Aceite de Motor y Filtro",
        "Alineación y Balanceo",
        "Frenos y Suspensión",
        "Batería y Sistema Eléctrico",
        "Mantenimiento General",
        "Otro"
    ),
    val workshopName: String = "",
    val serviceDate: String = "",
    val mileage: String = "",
    val totalCostPen: String = "",
    val notes: String = "",
    val attachedReceiptName: String? = null,
    val updateBaseOdometer: Boolean = false,
    val resetIntervalCounter: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
