package com.tuxlogic.shiftiq.mobile.feature.inventory.presentation

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError

internal fun mapInventoryError(error: AppError): String {
    val raw = listOfNotNull(
        error.message,
        (error as? AppError.Conflict)?.details,
        (error as? AppError.Validation)?.details
    ).joinToString(" ").lowercase()

    return when (error) {
        is AppError.Conflict -> when {
            raw.contains("sku") ->
                "Ya existe un producto con este SKU en la sucursal."
            raw.contains("asociad") ||
                raw.contains("work order") ||
                raw.contains("en uso") ||
                raw.contains("in use") ||
                raw.contains("orden") ->
                "No se puede eliminar el producto porque está asociado a órdenes de trabajo."
            else -> error.message
        }
        is AppError.Validation -> when {
            raw.contains("stock") || raw.contains("insufficient") || raw.contains("negativ") ->
                "No hay stock suficiente. No se permite dejar el saldo en negativo."
            else -> error.message
        }
        else -> error.message
    }
}

internal fun isDuplicateSkuError(message: String?): Boolean =
    message?.contains("SKU", ignoreCase = true) == true

internal fun isProductInUseError(message: String?): Boolean {
    if (message.isNullOrBlank()) return false
    val raw = message.lowercase()
    return raw.contains("órdenes") ||
        raw.contains("ordenes") ||
        raw.contains("asociad") ||
        raw.contains("en uso") ||
        raw.contains("work order")
}

internal fun isNegativeStockError(message: String?): Boolean {
    if (message.isNullOrBlank()) return false
    val raw = message.lowercase()
    return raw.contains("negativ") || raw.contains("stock suficiente") || raw.contains("insufficient")
}
