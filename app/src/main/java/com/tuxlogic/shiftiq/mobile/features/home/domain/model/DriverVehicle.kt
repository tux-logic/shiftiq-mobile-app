package com.tuxlogic.shiftiq.mobile.features.home.domain.model

data class DriverVehicle(
    val id: String,
    val plateNumber: String,
    val brand: String,
    val model: String,
    val year: Int,
    val vin: String,
    val photoUrl: String? = null
) {
    val displayName: String
        get() = "$brand $model ($year)"
        
    val formattedPlate: String
        get() = plateNumber.uppercase()
}
