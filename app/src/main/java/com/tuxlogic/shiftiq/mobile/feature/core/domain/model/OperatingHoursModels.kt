package com.tuxlogic.shiftiq.mobile.feature.core.domain.model

enum class OperatingHoursScope {
    BRANCH,
    WORKSHOP
}

data class OperatingHoursDay(
    val dayOfWeek: String,
    val open: Boolean,
    val openTime: String? = null,
    val closeTime: String? = null,
    val slotDurationMinutes: Int = 60,
    val source: String? = null
)

data class OperatingHoursException(
    val id: String,
    val date: String,
    val open: Boolean,
    val openTime: String? = null,
    val closeTime: String? = null,
    val reason: String? = null
)

data class OperatingHoursExceptionInput(
    val date: String,
    val open: Boolean,
    val openTime: String? = null,
    val closeTime: String? = null,
    val reason: String? = null
)
