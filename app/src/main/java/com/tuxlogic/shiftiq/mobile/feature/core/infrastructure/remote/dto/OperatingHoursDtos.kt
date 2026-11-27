package com.tuxlogic.shiftiq.mobile.feature.core.infrastructure.remote.dto

import com.google.gson.annotations.SerializedName
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursDay
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursException
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursExceptionInput

data class OperatingHoursDayDto(
    @SerializedName("dayOfWeek") val dayOfWeek: String,
    @SerializedName("open") val open: Boolean,
    @SerializedName("openTime") val openTime: String? = null,
    @SerializedName("closeTime") val closeTime: String? = null,
    @SerializedName("slotDurationMinutes") val slotDurationMinutes: Int? = 60,
    @SerializedName("source") val source: String? = null
) {
    fun toDomain() = OperatingHoursDay(
        dayOfWeek = dayOfWeek,
        open = open,
        openTime = openTime,
        closeTime = closeTime,
        slotDurationMinutes = slotDurationMinutes ?: 60,
        source = source
    )
}

data class OperatingHoursExceptionDto(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("open") val open: Boolean,
    @SerializedName("openTime") val openTime: String? = null,
    @SerializedName("closeTime") val closeTime: String? = null,
    @SerializedName("reason") val reason: String? = null
) {
    fun toDomain() = OperatingHoursException(
        id = id,
        date = date,
        open = open,
        openTime = openTime,
        closeTime = closeTime,
        reason = reason
    )
}

data class OperatingHoursRequestDto(
    @SerializedName("days") val days: List<OperatingHoursDayDto>
)

data class OperatingHoursExceptionRequestDto(
    @SerializedName("date") val date: String,
    @SerializedName("open") val open: Boolean,
    @SerializedName("openTime") val openTime: String?,
    @SerializedName("closeTime") val closeTime: String?,
    @SerializedName("reason") val reason: String?
) {
    companion object {
        fun from(input: OperatingHoursExceptionInput) = OperatingHoursExceptionRequestDto(
            date = input.date,
            open = input.open,
            openTime = if (input.open) input.openTime else null,
            closeTime = if (input.open) input.closeTime else null,
            reason = input.reason?.trim()?.takeIf(String::isNotEmpty)
        )
    }
}
