package com.tuxlogic.shiftiq.mobile.feature.iam.domain.model

import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId

data class User(
    val id: UserId,
    val email: String,
    val role: Role
)
