package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history

data class VoucherTableItem(
    val quantity: Int,
    val description: String,
    val unitPrice: Double,
    val totalPrice: Double
)

data class ServiceVoucherPdfUiState(
    val voucherId: String = "",
    val companyName: String = "",
    val tradeName: String = "",
    val ruc: String = "",
    val address: String = "",
    val voucherType: String = "",
    val voucherNumber: String = "",
    val status: String = "",
    val emissionDate: String = "",
    val clientName: String = "",
    val clientDni: String = "",
    val vehicleInfo: String = "",
    val odometerKm: Int? = null,
    val serviceReason: String = "",
    val items: List<VoucherTableItem> = emptyList(),
    val taxableAmount: Double? = null,
    val igvAmount: Double? = null,
    val totalPaid: Double? = null,
    val balance: Double? = null,
    val paymentInfo: String = "",
    val sunatHash: String = "",
    val pdfUrl: String? = null,
    val zoomPercent: Int = 100,
    val isSavedConfirmationVisible: Boolean = false,
    val savedFileName: String = "",
    val savedFilePath: String = "",
    val fileSizeText: String = "",
    val fileFormatText: String = "",
    val isLoading: Boolean = true,
    val isLoaded: Boolean = false,
    val errorMessage: String? = null
)
