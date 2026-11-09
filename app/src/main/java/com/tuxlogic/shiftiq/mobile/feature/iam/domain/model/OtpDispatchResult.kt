package com.tuxlogic.shiftiq.mobile.feature.iam.domain.model

data class OtpDispatchResult(
    val message: String,
    val expiresInSeconds: Long? = null
)
