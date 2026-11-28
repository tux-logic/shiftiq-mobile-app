package com.tuxlogic.shiftiq.mobile.feature.billing.presentation

import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrder

internal enum class VoucherDemoItemType {
    SERVICE,
    SPARE_PART
}

internal data class VoucherDemoItem(
    val description: String,
    val type: VoucherDemoItemType,
    val quantity: Int,
    val unitPrice: Double
) {
    val totalPrice: Double get() = quantity * unitPrice
}

/** Demo values mirrored from Flutter's CheckoutVoucherPage. */
internal object VoucherCheckoutDemoData {
    const val customerName = "Carlos Mendoza"
    const val invoiceCustomerName = "Transportes Rápidos Lima S.A.C."
    const val dni = "45678901"
    const val ruc = "20601893451"
    const val fiscalAddress = "Av. Universitaria 1240, San Martín de Porres, Lima"
    const val vehicle = "Toyota Hilux (ABC-123)"
    const val workOrderNumber = "1042"

    val items = listOf(
        VoucherDemoItem(
            description = "Diagnóstico y Escaneo Computarizado OBD-II",
            type = VoucherDemoItemType.SERVICE,
            quantity = 1,
            unitPrice = 80.0
        ),
        VoucherDemoItem(
            description = "Cambio de pastillas delanteras",
            type = VoucherDemoItemType.SERVICE,
            quantity = 1,
            unitPrice = 120.0
        ),
        VoucherDemoItem(
            description = "Pastillas Bosch Delanteras (Repuesto)",
            type = VoucherDemoItemType.SPARE_PART,
            quantity = 1,
            unitPrice = 100.0
        ),
        VoucherDemoItem(
            description = "Rectificado de discos ventilados delanteros",
            type = VoucherDemoItemType.SERVICE,
            quantity = 1,
            unitPrice = 150.0
        )
    )

    fun itemsFor(order: WorkOrder): List<VoucherDemoItem> {
        val workOrderItems = order.tasks.flatMap { task ->
            buildList {
                add(
                    VoucherDemoItem(
                        description = task.description,
                        type = VoucherDemoItemType.SERVICE,
                        quantity = 1,
                        unitPrice = task.price.amount.toDouble()
                    )
                )
                task.products.forEach { product ->
                    add(
                        VoucherDemoItem(
                            description = product.productName,
                            type = VoucherDemoItemType.SPARE_PART,
                            quantity = product.quantity,
                            unitPrice = product.unitPrice.amount.toDouble()
                        )
                    )
                }
            }
        }
        return workOrderItems.ifEmpty { items }
    }
}
