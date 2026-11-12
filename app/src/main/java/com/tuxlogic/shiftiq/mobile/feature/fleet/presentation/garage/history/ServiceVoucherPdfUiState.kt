package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

data class VoucherTableItem(
    val quantity: Int,
    val description: String,
    val unitPrice: Double,
    val totalPrice: Double
)

data class ServiceVoucherPdfUiState(
    val voucherId: String = "v-0004921",
    val companyName: String = "SILVA S.A.C.",
    val tradeName: String = "TALLER AUTOMOTRIZ SILVA S.A.C.",
    val ruc: String = "20608912341",
    val address: String = "Av. La Marina 2450, San Miguel, Lima",
    val voucherType: String = "FACTURA ELECTRÓNICA",
    val voucherNumber: String = "F001-0004921",
    val emissionDate: String = "15/02/2026",
    val clientName: String = "Rodrigo Morales",
    val clientDni: String = "72819230",
    val vehicleInfo: String = "Kia Rio 2019 • ABC-123",
    val odometerKm: Int = 45102,
    val serviceReason: String = "Mantenimiento Preventivo Periódico (45,000 km)",
    val items: List<VoucherTableItem> = listOf(
        VoucherTableItem(1, "Aceite Sintético 5W-30 (4 Qt) Motul", 140.00, 140.00),
        VoucherTableItem(1, "Filtro de Aceite OEM Kia", 35.00, 35.00),
        VoucherTableItem(1, "Filtro de Aire Motor", 45.00, 45.00),
        VoucherTableItem(1, "Mano de Obra Mantenimiento y Rotación", 60.00, 60.00)
    ),
    val taxableAmount: Double = 237.29,
    val igvAmount: Double = 42.71,
    val totalPaid: Double = 280.00,
    val paymentInfo: String = "Tarjeta Débito Visa (Aprobada #99412)",
    val sunatHash: String = "a8f4c391e3b092f67de4",
    val zoomPercent: Int = 100,
    val isSavedConfirmationVisible: Boolean = false,
    val savedFileName: String = "Informe_Tecnico_P0135_ABC123.pdf",
    val savedFilePath: String = "/Almacenamiento interno/Download/ShiftIq/",
    val fileSizeText: String = "142 KB",
    val fileFormatText: String = "PDF/A-1b",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
