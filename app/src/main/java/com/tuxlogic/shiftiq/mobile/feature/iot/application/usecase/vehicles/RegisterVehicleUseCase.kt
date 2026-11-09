package com.tuxlogic.shiftiq.mobile.feature.iot.application.usecase.vehicles

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.CustomerId
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.PlateNumber
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vehicle
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.model.Vin
import com.tuxlogic.shiftiq.mobile.feature.iot.domain.repository.IotRepository
import javax.inject.Inject

class RegisterVehicleUseCase @Inject constructor(
    private val iotRepository: IotRepository
) {
    suspend operator fun invoke(
        plateNumber: String,
        brand: String,
        model: String,
        year: Int,
        vin: String,
        photoUrl: String? = null,
        customerId: CustomerId? = null
    ): AppResult<Vehicle> {
        if (!PlateNumber.isValid(plateNumber)) {
            return AppResult.Failure(
                AppError.Validation(details = "plateNumber", message = "Plate number length must be between 5 and 10 characters")
            )
        }
        if (!Vin.isValid(vin)) {
            return AppResult.Failure(
                AppError.Validation(details = "vin", message = "Invalid VIN format: must be 17 characters without I, O, Q")
            )
        }
        if (brand.trim().isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "brand", message = "Vehicle brand is required")
            )
        }
        if (model.trim().isBlank()) {
            return AppResult.Failure(
                AppError.Validation(details = "model", message = "Vehicle model is required")
            )
        }
        if (year !in 1900..2100) {
            return AppResult.Failure(
                AppError.Validation(details = "year", message = "Year must be between 1900 and 2100")
            )
        }

        return iotRepository.registerVehicle(
            plateNumber = PlateNumber(plateNumber),
            brand = brand.trim(),
            model = model.trim(),
            year = year,
            vin = Vin(vin),
            photoUrl = photoUrl?.trim(),
            customerId = customerId
        )
    }
}
