package com.tuxlogic.shiftiq.mobile.feature.iam

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.api.AuthApiService
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.AuthenticatedUserResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.MessageResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.SendOtpResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.UserResourceDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.remote.dto.VerifyOtpResponseDto
import com.tuxlogic.shiftiq.mobile.feature.iam.infrastructure.repository.AuthRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

class AuthRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val authApiService: AuthApiService = mockk()
    private val sessionDataStore: SessionDataStore = mockk(relaxed = true)

    private lateinit var repository: AuthRepositoryImpl

    private val userUuid = UUID.randomUUID()
    private val userId = UserId(userUuid)

    private val sampleAuthUserDto = AuthenticatedUserResponseDto(
        id = userUuid.toString(),
        email = "user@example.com",
        role = "ROLE_USER",
        accessToken = "access-123",
        refreshToken = "refresh-456"
    )

    private val sampleUserDto = UserResourceDto(
        id = userUuid.toString(),
        email = "user@example.com",
        role = "ROLE_USER"
    )

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(authApiService, sessionDataStore, dispatchers)
    }

    @Test
    fun `login saves session and returns AuthenticatedUser`() = runTest(testDispatcher) {
        coEvery { authApiService.login(any()) } returns Response.success(sampleAuthUserDto)

        val result = repository.login("user@example.com", "Password123!")

        assertTrue(result is AppResult.Success)
        val user = (result as AppResult.Success).data
        assertEquals(userId, user.id)
        assertEquals("user@example.com", user.email)
        coVerify(exactly = 1) {
            sessionDataStore.saveSession(
                accessToken = "access-123",
                refreshToken = "refresh-456",
                userId = userId,
                email = "user@example.com",
                role = Role.ROLE_USER
            )
        }
    }

    @Test
    fun `loginWithGoogle saves session and returns AuthenticatedUser`() = runTest(testDispatcher) {
        coEvery { authApiService.loginWithGoogle(any()) } returns Response.success(sampleAuthUserDto)

        val result = repository.loginWithGoogle("google-token-xyz")

        assertTrue(result is AppResult.Success)
        val user = (result as AppResult.Success).data
        assertEquals(userId, user.id)
        coVerify(exactly = 1) { authApiService.loginWithGoogle(match { it.idToken == "google-token-xyz" }) }
        coVerify(exactly = 1) { sessionDataStore.saveSession(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `register creates and returns domain User`() = runTest(testDispatcher) {
        coEvery { authApiService.register(any()) } returns Response.success(sampleUserDto)

        val result = repository.register("user@example.com", "Password123!")

        assertTrue(result is AppResult.Success)
        val user = (result as AppResult.Success).data
        assertEquals(userId, user.id)
        assertEquals(Role.ROLE_USER, user.role)
        coVerify(exactly = 1) { authApiService.register(match {
            it.email == "user@example.com" && it.password == "Password123!"
        }) }
    }

    @Test
    fun `logout revokes refresh token and clears session`() = runTest(testDispatcher) {
        coEvery { sessionDataStore.sessionState } returns flowOf(SessionState(refreshToken = "ref-123"))
        coEvery { authApiService.logout(any()) } returns Response.success(Unit)

        val result = repository.logout()

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { authApiService.logout(match { it.refreshToken == "ref-123" }) }
        coVerify(exactly = 1) { sessionDataStore.clearSession() }
    }

    @Test
    fun `sendOtp dispatches request and returns OtpDispatchResult`() = runTest(testDispatcher) {
        coEvery { authApiService.sendOtp(any()) } returns Response.success(Unit)

        val result = repository.sendOtp("user@example.com")

        assertTrue(result is AppResult.Success)
        val otpResult = (result as AppResult.Success).data
        assertEquals("Código enviado exitosamente", otpResult.message)
        assertEquals(300L, otpResult.expiresInSeconds)
        coVerify(exactly = 1) { authApiService.sendOtp(match { it.email == "user@example.com" }) }
    }

    @Test
    fun `verifyOtp returns boolean verification status`() = runTest(testDispatcher) {
        coEvery { authApiService.verifyOtp(any()) } returns Response.success(Unit)

        val result = repository.verifyOtp("user@example.com", "123456")

        assertTrue(result is AppResult.Success)
        assertTrue((result as AppResult.Success).data)
        coVerify(exactly = 1) { authApiService.verifyOtp(match { it.email == "user@example.com" && it.code == "123456" }) }
    }

    @Test
    fun `requestPasswordRecovery sends email recovery request`() = runTest(testDispatcher) {
        coEvery { authApiService.requestPasswordRecovery(any()) } returns Response.success(Unit)

        val result = repository.requestPasswordRecovery("user@example.com")

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { authApiService.requestPasswordRecovery(match { it.email == "user@example.com" }) }
    }

    @Test
    fun `resetPassword sends token and password update`() = runTest(testDispatcher) {
        val dto = MessageResponseDto(message = "Password updated successfully")
        coEvery { authApiService.resetPassword(any()) } returns Response.success(dto)

        val result = repository.resetPassword("tok-1", "NewPassword123!")

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { authApiService.resetPassword(match {
            it.token == "tok-1" && it.newPassword == "NewPassword123!"
        }) }
    }

    @Test
    fun `getUserById returns domain User`() = runTest(testDispatcher) {
        coEvery { authApiService.getUserById(userUuid.toString()) } returns Response.success(sampleUserDto)

        val result = repository.getUserById(userId)

        assertTrue(result is AppResult.Success)
        assertEquals(userId, (result as AppResult.Success).data.id)
        coVerify(exactly = 1) { authApiService.getUserById(userUuid.toString()) }
    }

    @Test
    fun `getUserByEmail returns domain User`() = runTest(testDispatcher) {
        coEvery { authApiService.getUserByEmail("user@example.com") } returns Response.success(sampleUserDto)

        val result = repository.getUserByEmail("user@example.com")

        assertTrue(result is AppResult.Success)
        assertEquals(userId, (result as AppResult.Success).data.id)
        coVerify(exactly = 1) { authApiService.getUserByEmail("user@example.com") }
    }

    @Test
    fun `updateEmail updates user email and rotates session tokens`() = runTest(testDispatcher) {
        val updatedDto = sampleAuthUserDto.copy(email = "new@example.com", accessToken = "new-access", refreshToken = "new-refresh")
        coEvery { authApiService.updateEmail(userUuid.toString(), any()) } returns Response.success(updatedDto)

        val result = repository.updateEmail(userId, "new@example.com")

        assertTrue(result is AppResult.Success)
        val user = (result as AppResult.Success).data
        assertEquals("new@example.com", user.email)
        coVerify(exactly = 1) {
            sessionDataStore.saveSession(
                accessToken = "new-access",
                refreshToken = "new-refresh",
                userId = userId,
                email = "new@example.com",
                role = Role.ROLE_USER
            )
        }
    }

    @Test
    fun `updatePassword sends current and new passwords`() = runTest(testDispatcher) {
        val dto = MessageResponseDto(message = "Password updated successfully")
        coEvery { authApiService.updatePassword(userUuid.toString(), any()) } returns Response.success(dto)

        val result = repository.updatePassword(userId, "OldPass1!", "NewPass2@")

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { authApiService.updatePassword(userUuid.toString(), match {
            it.currentPassword == "OldPass1!" && it.newPassword == "NewPass2@"
        }) }
    }

    @Test
    fun `401 error response returns Unauthorized failure`() = runTest(testDispatcher) {
        val errorResponse = Response.error<AuthenticatedUserResponseDto>(
            401,
            "{\"code\":\"UNAUTHORIZED\",\"message\":\"Bad credentials\"}".toResponseBody("application/json".toMediaType())
        )
        coEvery { authApiService.login(any()) } returns errorResponse

        val result = repository.login("user@example.com", "wrong")

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Unauthorized)
    }
}
