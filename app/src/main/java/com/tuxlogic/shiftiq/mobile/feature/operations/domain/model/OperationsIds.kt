package com.tuxlogic.shiftiq.mobile.feature.operations.domain.model

import java.util.UUID

@JvmInline
value class WorkOrderId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )
    override fun toString(): String = value.toString()
}

@JvmInline
value class TaskId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )
    override fun toString(): String = value.toString()
}

@JvmInline
value class ServiceId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )
    override fun toString(): String = value.toString()
}

@JvmInline
value class ProductId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )
    override fun toString(): String = value.toString()
}
