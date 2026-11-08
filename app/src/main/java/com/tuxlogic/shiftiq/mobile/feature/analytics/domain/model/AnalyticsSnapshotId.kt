package com.tuxlogic.shiftiq.mobile.feature.analytics.domain.model

import java.util.UUID

@JvmInline
value class AnalyticsSnapshotId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): AnalyticsSnapshotId = AnalyticsSnapshotId(id)
        operator fun invoke(id: String): AnalyticsSnapshotId = fromString(id)
    }
}
