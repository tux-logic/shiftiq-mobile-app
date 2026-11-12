package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.core.model.VehicleId

data class Vehicle(
    val id: VehicleId,
    val plateNumber: PlateNumber,
    val brand: String,
    val model: String,
    val year: Int,
    val vin: Vin,
    val photoUrl: String? = null,
    val customerId: CustomerId? = null
)
