package com.tuxlogic.shiftiq.mobile.feature.iot.domain.model

import java.util.UUID

@JvmInline
value class DeviceId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): DeviceId = DeviceId(id)
        operator fun invoke(id: String): DeviceId = fromString(id)
    }
}

@JvmInline
value class RegistrationId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): RegistrationId = RegistrationId(id)
        operator fun invoke(id: String): RegistrationId = fromString(id)
    }
}

@JvmInline
value class SnapshotId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): SnapshotId = SnapshotId(id)
        operator fun invoke(id: String): SnapshotId = fromString(id)
    }
}

@JvmInline
value class DtcAlertId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): DtcAlertId = DtcAlertId(id)
        operator fun invoke(id: String): DtcAlertId = fromString(id)
    }
}
