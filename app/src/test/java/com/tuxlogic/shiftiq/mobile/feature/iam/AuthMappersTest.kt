package com.tuxlogic.shiftiq.mobile.feature.iam

import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.AuthenticatedUserResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SendOtpResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UserResourceDto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class AuthMappersTest {

    @Test
    fun `UserResourceDto toDomain maps all fields properly`() {
        val uuid = UUID.randomUUID()
        val dto = UserResourceDto(
            id = uuid.toString(),
            email = "test@example.com",
            role = "ROLE_OWNER"
        )

        val domain = dto.toDomain()

        assertEquals(UserId(uuid), domain.id)
        assertEquals("test@example.com", domain.email)
        assertEquals(Role.ROLE_OWNER, domain.role)
    }

    @Test
    fun `UserResourceDto toDomain falls back to ROLE_USER when role is null or invalid`() {
        val uuid = UUID.randomUUID()
        val dto = UserResourceDto(
            id = uuid.toString(),
            email = "user@example.com",
            role = null
        )

        val domain = dto.toDomain()
        assertEquals(Role.ROLE_USER, domain.role)
    }

    @Test
    fun `AuthenticatedUserResponseDto toDomain maps tokens and user details`() {
        val uuid = UUID.randomUUID()
        val dto = AuthenticatedUserResponseDto(
            id = uuid.toString(),
            email = "admin@example.com",
            role = "ROLE_ADMIN",
            accessToken = "access-token-123",
            refreshToken = "refresh-token-456"
        )

        val domain = dto.toDomain()

        assertEquals(UserId(uuid), domain.id)
        assertEquals("admin@example.com", domain.email)
        assertEquals(Role.ROLE_ADMIN, domain.role)
        assertEquals("access-token-123", domain.accessToken)
        assertEquals("refresh-token-456", domain.refreshToken)
    }

    @Test
    fun `SendOtpResponseDto toDomain maps message and expiresInSeconds`() {
        val dto = SendOtpResponseDto(
            message = "OTP sent successfully",
            expiresInSeconds = 300L
        )

        val domain = dto.toDomain()

        assertEquals("OTP sent successfully", domain.message)
        assertEquals(300L, domain.expiresInSeconds)
    }
}
