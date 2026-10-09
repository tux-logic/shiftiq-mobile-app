package com.tuxlogic.shiftiq.mobile.core.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

/**
 * Value Object para representar montos monetarios en la aplicación.
 * Garantiza precisión decimal sin pérdidas por aritmética de punto flotante.
 */
data class Money(
    val amount: BigDecimal,
    val currency: String = "PEN"
) : Comparable<Money> {

    constructor(amountDouble: Double, currency: String = "PEN") : this(
        BigDecimal.valueOf(amountDouble).setScale(2, RoundingMode.HALF_UP),
        currency
    )

    constructor(amountLong: Long, currency: String = "PEN") : this(
        BigDecimal.valueOf(amountLong).setScale(2, RoundingMode.HALF_UP),
        currency
    )

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "No se pueden sumar montos de monedas distintas: $currency y ${other.currency}" }
        return Money(amount.add(other.amount), currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "No se pueden restar montos de monedas distintas: $currency y ${other.currency}" }
        return Money(amount.subtract(other.amount), currency)
    }

    operator fun times(factor: BigDecimal): Money =
        Money(amount.multiply(factor).setScale(2, RoundingMode.HALF_UP), currency)

    operator fun times(factor: Int): Money =
        Money(amount.multiply(BigDecimal(factor)).setScale(2, RoundingMode.HALF_UP), currency)

    override fun compareTo(other: Money): Int {
        require(currency == other.currency) { "No se pueden comparar montos de monedas distintas: $currency y ${other.currency}" }
        return amount.compareTo(other.amount)
    }

    fun formatted(): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
        return try {
            format.format(amount)
        } catch (_: Exception) {
            "$currency ${amount.setScale(2, RoundingMode.HALF_UP)}"
        }
    }

    companion object {
        val ZERO = Money(BigDecimal.ZERO)

        fun of(amount: Double, currency: String = "PEN") = Money(amount, currency)
    }
}
