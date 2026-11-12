package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.external

data class ExternalServiceRegistrationUiState(
    val serviceType: String = "Cambio de Aceite de Motor y Filtro",
    val availableServiceTypes: List<String> = listOf(
        "Cambio de Aceite de Motor y Filtro",
        "Alineación y Balanceo",
        "Frenos y Suspensión",
        "Batería y Sistema Eléctrico",
        "Mantenimiento General",
        "Otro"
    ),
    val workshopName: String = "",
    val serviceDate: String = "12 / Mar / 2026",
    val mileage: String = "48,150",
    val totalCostPen: String = "0.00",
    val notes: String = "",
    val attachedReceiptName: String? = null,
    val updateBaseOdometer: Boolean = true,
    val resetIntervalCounter: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
