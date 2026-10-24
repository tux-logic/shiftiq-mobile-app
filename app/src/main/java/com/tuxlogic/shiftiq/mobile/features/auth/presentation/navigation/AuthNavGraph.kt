package com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.login.LoginScreen
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.recovery.RecoveryScreen
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.register.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
data object AuthNavGraphRoute

@Serializable
data object LoginRoute

@Serializable
data object SignInRoute

@Serializable
data object SignUpRoute

@Serializable
data object RegisterRoute

@Serializable
data object RecoveryRoute

@Serializable
data object ForgotPasswordRoute

@Serializable
data object WorkshopRegisterRoute

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation<AuthNavGraphRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Navigate to Driver Main/Dashboard
                },
                onForgotPasswordClick = {
                    navController.navigate(RecoveryRoute)
                },
                onRegisterClick = {
                    navController.navigate(RegisterRoute)
                }
            )
        }
        composable<SignInRoute> {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Navigate to Driver Main/Dashboard
                },
                onForgotPasswordClick = {
                    navController.navigate(RecoveryRoute)
                },
                onRegisterClick = {
                    navController.navigate(RegisterRoute)
                }
            )
        }
        composable<RegisterRoute> {
            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onNavigateToRecovery = {
                    navController.navigate(RecoveryRoute)
                },
                onRegistrationSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo<RegisterRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<SignUpRoute> {
            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onNavigateToRecovery = {
                    navController.navigate(RecoveryRoute)
                },
                onRegistrationSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo<SignUpRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<RecoveryRoute> {
            RecoveryScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<ForgotPasswordRoute> {
            RecoveryScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<WorkshopRegisterRoute> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Portal de Registro de Taller B2B")
            }
        }
    }
}
