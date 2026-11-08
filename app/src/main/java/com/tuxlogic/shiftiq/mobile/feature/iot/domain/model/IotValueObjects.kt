package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

@JvmInline
value class MacAddress(val value: String) {
    init {
        val trimmed = value.trim()
        require(trimmed.matches(MAC_REGEX)) { "Invalid MAC address format: $value" }
    }

    override fun toString(): String = value.trim().uppercase()

    companion object {
        private val MAC_REGEX = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    }
}

@JvmInline
value class Vin(val value: String) {
    init {
        val trimmed = value.trim()
        require(trimmed.matches(VIN_REGEX)) { "Invalid VIN format: must be 17 characters without I, O, Q" }
    }

    override fun toString(): String = value.trim().uppercase()

    companion object {
        private val VIN_REGEX = Regex("^[A-HJ-NPR-Z0-9]{17}$", RegexOption.IGNORE_CASE)
    }
}

@JvmInline
value class PlateNumber(val value: String) {
    init {
        val trimmed = value.trim()
        require(trimmed.isNotBlank()) { "Plate number cannot be blank" }
        require(trimmed.length in 5..10) { "Plate number length must be between 5 and 10 characters" }
    }

    override fun toString(): String = value.trim().uppercase()
}
