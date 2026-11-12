package com.tuxlogic.shiftiq.mobile.feature.iam

import com.tuxlogic.shiftiq.mobile.core.common.event.DomainEventBus
import com.tuxlogic.shiftiq.mobile.core.common.event.UserLoggedOutEvent
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.LogoutUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LogoutUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val domainEventBus: DomainEventBus = mockk(relaxed = true)

    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        logoutUseCase = LogoutUseCase(authRepository, domainEventBus)
    }

    @Test
    fun `invoke emits UserLoggedOutEvent on successful logout`() = runTest {
        coEvery { authRepository.logout() } returns AppResult.Success(Unit)

        val result = logoutUseCase()

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { authRepository.logout() }
        coVerify(exactly = 1) { domainEventBus.emit(ofType<UserLoggedOutEvent>()) }
    }

    @Test
    fun `invoke does not emit UserLoggedOutEvent on logout failure`() = runTest {
        coEvery { authRepository.logout() } returns AppResult.Failure(AppError.Unexpected("Error"))

        val result = logoutUseCase()

        assertTrue(result is AppResult.Failure)
        coVerify(exactly = 1) { authRepository.logout() }
        coVerify(exactly = 0) { domainEventBus.emit(ofType<UserLoggedOutEvent>()) }
    }
}
