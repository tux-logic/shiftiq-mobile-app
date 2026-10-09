package com.tuxlogic.shiftiq.mobile.feature.iam

import app.cash.turbine.test
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId
import com.tuxlogic.shiftiq.mobile.core.testing.MainDispatcherRule
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.model.AuthenticatedUser
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase.LoginUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(loginUseCase)
    }

    @Test
    fun `initial state is empty and idle`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.email)
            assertEquals("", state.password)
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `onEmailChanged and onPasswordChanged update state correctly`() = runTest {
        viewModel.onEmailChanged("user@shiftiq.com")
        viewModel.onPasswordChanged("secret123")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("user@shiftiq.com", state.email)
            assertEquals("secret123", state.password)
        }
    }

    @Test
    fun `when login succeeds should update state to success`() = runTest {
        val user = AuthenticatedUser(
            id = UserId("u1000000-0000-0000-0000-000000000001"),
            email = "user@shiftiq.com",
            role = Role.ROLE_OWNER,
            accessToken = "token",
            refreshToken = "refresh"
        )
        coEvery { loginUseCase("user@shiftiq.com", "secret123") } returns AppResult.Success(user)

        viewModel.onEmailChanged("user@shiftiq.com")
        viewModel.onPasswordChanged("secret123")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSuccess)
            assertFalse(state.isLoading)
            assertEquals(Role.ROLE_OWNER, state.userRole)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `when login fails with Unauthorized should display friendly error message`() = runTest {
        coEvery { loginUseCase("user@shiftiq.com", "wrong") } returns AppResult.Failure(AppError.Unauthorized())

        viewModel.onEmailChanged("user@shiftiq.com")
        viewModel.onPasswordChanged("wrong")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSuccess)
            assertFalse(state.isLoading)
            assertEquals("Credenciales incorrectas. Verifica tu email y contraseña.", state.errorMessage)
        }
    }
}
