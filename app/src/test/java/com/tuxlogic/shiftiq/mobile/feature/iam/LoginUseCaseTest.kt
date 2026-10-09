package com.tuxlogic.shiftiq.mobile.feature.iam

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase.LoginUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `when email is blank should return validation error without calling repository`() = runTest {
        val result = loginUseCase("", "password123")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("email", (error as AppError.Validation).details)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `when email has invalid format should return validation error`() = runTest {
        val result = loginUseCase("not-an-email", "password123")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("email", (error as AppError.Validation).details)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `when password is blank should return validation error`() = runTest {
        val result = loginUseCase("admin@shiftiq.com", "")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("password", (error as AppError.Validation).details)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `when password has less than 6 chars should return validation error`() = runTest {
        val result = loginUseCase("admin@shiftiq.com", "12345")
        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Validation)
        assertEquals("password", (error as AppError.Validation).details)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `when credentials are valid should call repository and return success`() = runTest {
        val expectedUser = AuthenticatedUser(
            id = UserId("u1000000-0000-0000-0000-000000000001"),
            email = "admin@shiftiq.com",
            role = Role.ROLE_OWNER,
            accessToken = "token123",
            refreshToken = "refresh123"
        )
        coEvery { authRepository.login("admin@shiftiq.com", "password123") } returns AppResult.Success(expectedUser)

        val result = loginUseCase("admin@shiftiq.com", "password123")

        assertTrue(result is AppResult.Success)
        assertEquals(expectedUser, (result as AppResult.Success).data)
        coVerify(exactly = 1) { authRepository.login("admin@shiftiq.com", "password123") }
    }
}
