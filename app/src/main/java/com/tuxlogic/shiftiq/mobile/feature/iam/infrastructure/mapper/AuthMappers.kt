package com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.mapper

import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.AuthenticatedUserResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SendOtpResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UserResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.OtpDispatchResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.User

fun UserResourceDto.toDomain(): User = User(
    id = UserId(id),
    email = email,
    role = role?.let { Role.fromName(it) } ?: Role.ROLE_USER
)

fun AuthenticatedUserResponseDto.toDomain(): AuthenticatedUser = AuthenticatedUser(
    id = UserId(id),
    email = email,
    role = Role.fromName(role) ?: Role.ROLE_USER,
    accessToken = accessToken,
    refreshToken = refreshToken
)

fun SendOtpResponseDto.toDomain(): OtpDispatchResult = OtpDispatchResult(
    message = message,
    expiresInSeconds = expiresInSeconds
)
