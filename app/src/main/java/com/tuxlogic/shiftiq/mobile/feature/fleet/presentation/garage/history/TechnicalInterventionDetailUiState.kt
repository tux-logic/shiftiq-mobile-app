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
    val expNumber: String = "#AL-2025-8841",
    val isSealed: Boolean = true,
    val statusText: String = "[ SERVICIO COMPLETADO ]",
    val odometerKm: Int = 41200,
    val serviceTitle: String = "Alineación Computarizada y Balanceo",
    val executionDate: String = "10 Nov 2025 • 10:45 AM",
    val workshopName: String = "Taller Multimarca San Miguel",
    val workshopAddress: String = "Av. Elmer Faucett 120 • San Miguel, Lima",
    val technicianName: String = "Carlos Mendoza",
    val technicianCert: String = "Cert. SAE Nivel 2",
    val totalCostPen: Double = 90.00,
    val paymentMethod: String = "Pago Presencial • Efectivo / POS",
    val laserCalibrations: List<CalibrationValueItem> = listOf(
        CalibrationValueItem("Combas Del. Izq.", "-0°45'", "0°02'", "[ Corregido ]"),
        CalibrationValueItem("Combas Del. Der.", "-1°10'", "0°00'", "[ Óptimo ]"),
        CalibrationValueItem("Divergencia / Toe", "+0°15'", "0°01'", "[ Alineado ]"),
        CalibrationValueItem("Desbalanceo Ruedas", "25g/15g", "0g/0g", "[ Plomado ]")
    ),
    val protocolOperations: List<ProtocolOperationItem> = listOf(
        ProtocolOperationItem("Medición en fosa con sensores láser 3D", "Calibración en las 4 ruedas en plano horizontal cero."),
        ProtocolOperationItem("Ajuste de terminales de dirección y contratuercas", "Torque nominal verificado según manual OEM."),
        ProtocolOperationItem("Balanceo dinámico computarizado en banco rotativo", "4 conjuntos llanta-aro equilibrados a cero estático/dinámico."),
        ProtocolOperationItem("Calibración de sensor de ángulo de dirección (SAS)", "Reinicio de punto cero computarizado vía puerto OBD-II.")
    ),
    val usedSupplies: List<UsedSupplyItem> = listOf(
        UsedSupplyItem("Plomos de contrapeso adhesivos (40g)", "Spray desengrasante y limpiador de roscas")
    ),
    val technicianObservation: String = "Se detectó leve desgaste interior en neumático delantero derecho preexistente por desalineación previa acumulada. Se recomienda rotación periódica al alcanzar los 45,000 km.",
    val warrantyPolicy: String = "Garantía activa de 3 meses o 3,000 km de alineación garantizada.",
    val voucherId: String = "v-0004921",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
