package com.tuxlogic.shiftiq.mobile.core.model

import java.util.UUID

/**
 * Value Objects tipados para identificadores de agregados en ShiftIQ.
 * Previenen errores de intercambio accidental de UUIDs en llamadas de servicio.
 */
@JvmInline
value class UserId(val value: UUID) {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))
    override fun toString(): String = value.toString()
}

@JvmInline
value class BranchId(val value: UUID) {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))
    override fun toString(): String = value.toString()
}

@JvmInline
value class WorkshopId(val value: UUID) {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))
    override fun toString(): String = value.toString()
}

@JvmInline
value class CustomerId(val value: UUID) {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))
    override fun toString(): String = value.toString()
}

@JvmInline
value class EmployeeId(val value: UUID) {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))
    override fun toString(): String = value.toString()
}

@JvmInline
value class VehicleId(val value: UUID) {
    constructor(uuidString: String) : this(UUID.fromString(uuidString))
    override fun toString(): String = value.toString()
}
