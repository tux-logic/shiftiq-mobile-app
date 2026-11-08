package com.tuxlogic.shiftiq.mobile.feature.inventory.domain.model

import java.util.UUID

@JvmInline
value class ProductId(val value: UUID) {
    constructor(idStr: String) : this(
        runCatching { UUID.fromString(idStr) }.getOrElse { UUID.nameUUIDFromBytes(idStr.toByteArray()) }
    )

    override fun toString(): String = value.toString()

    companion object {
        fun fromString(id: String): ProductId = ProductId(id)
        operator fun invoke(id: String): ProductId = fromString(id)
    }
}

@JvmInline
value class BatchId(val value: String) {
    init {
        require(value.isNotBlank()) { "Batch ID cannot be blank" }
    }

    override fun toString(): String = value
}

@JvmInline
value class Sku(val value: String) {
    init {
        val trimmed = value.trim()
        require(trimmed.isNotBlank()) { "SKU cannot be blank" }
        require(trimmed.length <= 50) { "SKU cannot exceed 50 characters" }
    }

    override fun toString(): String = value.trim().uppercase()
}
