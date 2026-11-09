package com.tuxlogic.shiftiq.mobile.feature.billing.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Money

data class PaymentPreference(
    val preferenceId: String,
    val initPoint: String,
    val sandboxInitPoint: String,
    val amount: Money,
    val currency: String,
    val externalReference: String
)
