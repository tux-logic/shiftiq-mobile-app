package com.tuxlogic.shiftiq.mobile.feature.iam

import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.OtpDispatchResult
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.User
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.GetUserByEmailUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.GetUserByIdUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.LoginWithGoogleUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.RegisterUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.RequestPasswordRecoveryUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.ResetPasswordUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.SendOtpUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.UpdateEmailUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.UpdatePasswordUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.VerifyOtpUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AuthUseCasesTest {

    private val repository: AuthRepository = mockk()

    private val userUuid = UUID.randomUUID()
    private val userId = UserId(userUuid)

    private val sampleUser = User(
        id = userId,
        email = "user@example.com",
        role = Role.ROLE_USER
    )

    private val sampleAuthUser = AuthenticatedUser(
        id = userId,
        email = "user@example.com",
        role = Role.ROLE_USER,
        accessToken = "jwt-access",
        refreshToken = "jwt-refresh"
    )

    @Before
    fun setUp() {
    }

    @Test
    fun `LoginWithGoogleUseCase returns authenticated user on valid token`() = runTest {
        val useCase = LoginWithGoogleUseCase(repository)
        coEvery { repository.loginWithGoogle("valid-token") } returns AppResult.Success(sampleAuthUser)

        val result = useCase("valid-token")

        assertTrue(result is AppResult.Success)
        assertEquals(sampleAuthUser, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.loginWithGoogle("valid-token") }
    }

    @Test
    fun `LoginWithGoogleUseCase rejects blank token`() = runTest {
        val useCase = LoginWithGoogleUseCase(repository)
        val result = useCase("")

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { repository.loginWithGoogle(any()) }
    }

    @Test
    fun `SendOtpUseCase calls repository with trimmed email`() = runTest {
        val useCase = SendOtpUseCase(repository)
        val otpResult = OtpDispatchResult("Sent", 300L)
        coEvery { repository.sendOtp("test@example.com") } returns AppResult.Success(otpResult)

        val result = useCase("  test@example.com  ")

        assertTrue(result is AppResult.Success)
        assertEquals(otpResult, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.sendOtp("test@example.com") }
    }

    @Test
    fun `VerifyOtpUseCase rejects code with length different than 6`() = runTest {
        val useCase = VerifyOtpUseCase(repository)
        val result = useCase("test@example.com", "123")

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        coVerify(exactly = 0) { repository.verifyOtp(any(), any()) }
    }

    @Test
    fun `VerifyOtpUseCase delegates to repository on valid code`() = runTest {
        val useCase = VerifyOtpUseCase(repository)
        coEvery { repository.verifyOtp("test@example.com", "123456") } returns AppResult.Success(true)

        val result = useCase("test@example.com", "123456")

        assertTrue(result is AppResult.Success)
        assertTrue((result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.verifyOtp("test@example.com", "123456") }
    }

    @Test
    fun `RegisterUseCase creates user successfully`() = runTest {
        val useCase = RegisterUseCase(repository)
        coEvery { repository.register("new@example.com", "Password123!") } returns AppResult.Success(sampleUser)

        val result = useCase("new@example.com", "Password123!")

        assertTrue(result is AppResult.Success)
        assertEquals(sampleUser, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.register("new@example.com", "Password123!") }
    }

    @Test
    fun `RequestPasswordRecoveryUseCase dispatches recovery request`() = runTest {
        val useCase = RequestPasswordRecoveryUseCase(repository)
        coEvery { repository.requestPasswordRecovery("user@example.com") } returns AppResult.Success(Unit)

        val result = useCase("user@example.com")

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { repository.requestPasswordRecovery("user@example.com") }
    }

    @Test
    fun `ResetPasswordUseCase resets password with valid token and password`() = runTest {
        val useCase = ResetPasswordUseCase(repository)
        coEvery { repository.resetPassword("valid-token", "NewPassword123!") } returns AppResult.Success(Unit)

        val result = useCase("valid-token", "NewPassword123!")

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { repository.resetPassword("valid-token", "NewPassword123!") }
    }

    @Test
    fun `GetUserByIdUseCase delegates to repository`() = runTest {
        val useCase = GetUserByIdUseCase(repository)
        coEvery { repository.getUserById(userId) } returns AppResult.Success(sampleUser)

        val result = useCase(userId)

        assertTrue(result is AppResult.Success)
        assertEquals(sampleUser, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.getUserById(userId) }
    }

    @Test
    fun `GetUserByEmailUseCase delegates to repository`() = runTest {
        val useCase = GetUserByEmailUseCase(repository)
        coEvery { repository.getUserByEmail("user@example.com") } returns AppResult.Success(sampleUser)

        val result = useCase("user@example.com")

        assertTrue(result is AppResult.Success)
        assertEquals(sampleUser, (result as AppResult.Success).data)
        coVerify(exactly = 1) { repository.getUserByEmail("user@example.com") }
    }

    @Test
    fun `UpdateEmailUseCase updates user email`() = runTest {
        val useCase = UpdateEmailUseCase(repository)
        val updatedAuthUser = sampleAuthUser.copy(email = "updated@example.com")
        coEvery { repository.updateEmail(userId, "updated@example.com") } returns AppResult.Success(updatedAuthUser)

        val result = useCase(userId, "updated@example.com")

        assertTrue(result is AppResult.Success)
        assertEquals("updated@example.com", (result as AppResult.Success).data.email)
        coVerify(exactly = 1) { repository.updateEmail(userId, "updated@example.com") }
    }

    @Test
    fun `UpdatePasswordUseCase changes user password`() = runTest {
        val useCase = UpdatePasswordUseCase(repository)
        coEvery { repository.updatePassword(userId, "OldPassword1!", "NewPassword2@") } returns AppResult.Success(Unit)

        val result = useCase(userId, "OldPassword1!", "NewPassword2@")

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { repository.updatePassword(userId, "OldPassword1!", "NewPassword2@") }
    }
}
