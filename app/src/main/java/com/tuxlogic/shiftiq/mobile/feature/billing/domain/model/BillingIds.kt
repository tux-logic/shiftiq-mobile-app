package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import java.util.UUID

@JvmInline
value class QuoteId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): QuoteId = QuoteId(id)
        operator fun invoke(id: String): QuoteId = fromString(id)
    }
}

@JvmInline
value class VoucherId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): VoucherId = VoucherId(id)
        operator fun invoke(id: String): VoucherId = fromString(id)
    }
}

@JvmInline
value class PaymentId(val value: String) {
    init {
        require(value.isNotBlank()) { "Payment ID cannot be blank" }
    }

    override fun toString(): String = value

    companion object {
        fun fromString(id: String): PaymentId = PaymentId(id)
        operator fun invoke(id: String): PaymentId = fromString(id)
    }
}
