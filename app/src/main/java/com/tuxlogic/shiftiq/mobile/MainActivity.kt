package com.tuxlogic.shiftiq.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQLoadingScreen
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.navigation.AppDestination
import com.tuxlogic.shiftiq.mobile.feature.iam.domain.usecase.LogoutUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch.BranchSelectionScreen
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch.BranchSelectionViewModel
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.dashboard.RoleDashboardScreen
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginScreen
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionDataStore: SessionDataStore

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShiftIQTheme {
                val sessionState by sessionDataStore.sessionState.collectAsState(initial = null)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val session = sessionState
                    if (session == null) {
                        ShiftIQLoadingScreen()
                    } else {
                        val navController = rememberNavController()
                        val scope = rememberCoroutineScope()

                        val startDestination = when {
                            !session.isAuthenticated -> AppDestination.Login.route
                            session.userRole == Role.ROLE_OWNER && session.activeBranchId == null -> AppDestination.BranchSelection.route
                            else -> AppDestination.Dashboard.route
                        }

                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            composable(AppDestination.Login.route) {
                                val loginViewModel: LoginViewModel = hiltViewModel()
                                val uiState by loginViewModel.uiState.collectAsState()

                                LoginScreen(
                                    uiState = uiState,
                                    onEmailChanged = loginViewModel::onEmailChanged,
                                    onPasswordChanged = loginViewModel::onPasswordChanged,
                                    onLoginClick = loginViewModel::login,
                                    onLoginSuccess = { role ->
                                        if (role == Role.ROLE_OWNER) {
                                            navController.navigate(AppDestination.BranchSelection.route) {
                                                popUpTo(AppDestination.Login.route) { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate(AppDestination.Dashboard.route) {
                                                popUpTo(AppDestination.Login.route) { inclusive = true }
                                            }
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate(AppDestination.Register.route)
                                    }
                                )
                            }

                            composable(AppDestination.Register.route) {
                                val registerViewModel: com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register.RegisterViewModel = hiltViewModel()
                                val uiState by registerViewModel.uiState.collectAsState()

                                com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register.RegisterScreen(
                                    uiState = uiState,
                                    onEmailChanged = registerViewModel::onEmailChanged,
                                    onPasswordChanged = registerViewModel::onPasswordChanged,
                                    onRoleSelected = registerViewModel::onRoleSelected,
                                    onRegisterClick = registerViewModel::register,
                                    onNavigateToLogin = { navController.popBackStack() },
                                    onRegisterSuccess = {
                                        navController.navigate(AppDestination.Dashboard.route) {
                                            popUpTo(AppDestination.Login.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(AppDestination.BranchSelection.route) {
                                val branchViewModel: BranchSelectionViewModel = hiltViewModel()
                                val uiState by branchViewModel.uiState.collectAsState()

                                BranchSelectionScreen(
                                    uiState = uiState,
                                    onBranchSelected = branchViewModel::onBranchSelected,
                                    onConfirmClick = branchViewModel::confirmBranchSelection,
                                    onConfirmed = {
                                        navController.navigate(AppDestination.Dashboard.route) {
                                            popUpTo(AppDestination.BranchSelection.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(AppDestination.Dashboard.route) {
                                RoleDashboardScreen(
                                    userRole = session.userRole,
                                    activeBranchId = session.activeBranchId,
                                    userId = session.userId,
                                    onNavigateToWorkshops = {
                                        navController.navigate(AppDestination.WorkshopList.route)
                                    },
                                    onNavigateToOwnerProfile = {
                                        navController.navigate(AppDestination.OwnerProfile.route)
                                    },
                                    onLogoutClick = {
                                        scope.launch {
                                            logoutUseCase()
                                            navController.navigate(AppDestination.Login.route) {
                                                popUpTo(navController.graph.id) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            // Bloque 2: Core (Talleres y Sedes)
                            composable(AppDestination.WorkshopList.route) {
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.WorkshopListViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.WorkshopListScreen(
                                    viewModel = viewModel,
                                    onNavigateToCreateWorkshop = { ownerId ->
                                        navController.navigate(AppDestination.CreateWorkshop.createRoute(ownerId))
                                    },
                                    onNavigateToOwnerProfile = {
                                        navController.navigate(AppDestination.OwnerProfile.route)
                                    },
                                    onNavigateToBranches = { workshopId ->
                                        navController.navigate(AppDestination.BranchManagement.createRoute(workshopId))
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.CreateWorkshop.route,
                                arguments = listOf(androidx.navigation.navArgument("ownerId") { type = androidx.navigation.NavType.StringType })
                            ) { backStackEntry ->
                                val ownerId = backStackEntry.arguments?.getString("ownerId") ?: ""
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.CreateWorkshopViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.CreateWorkshopScreen(
                                    ownerId = ownerId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onWorkshopCreated = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = AppDestination.BranchManagement.route,
                                arguments = listOf(androidx.navigation.navArgument("workshopId") { type = androidx.navigation.NavType.StringType })
                            ) { backStackEntry ->
                                val workshopId = backStackEntry.arguments?.getString("workshopId") ?: ""
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.BranchManagementViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.BranchManagementScreen(
                                    workshopId = workshopId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToCreateBranch = { wId ->
                                        navController.navigate(AppDestination.CreateBranch.createRoute(wId))
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.CreateBranch.route,
                                arguments = listOf(androidx.navigation.navArgument("workshopId") { type = androidx.navigation.NavType.StringType })
                            ) { backStackEntry ->
                                val workshopId = backStackEntry.arguments?.getString("workshopId") ?: ""
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.CreateBranchViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.CreateBranchScreen(
                                    workshopId = workshopId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onBranchCreated = { navController.popBackStack() }
                                )
                            }

                            composable(AppDestination.OwnerProfile.route) {
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.owner.OwnerProfileViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.owner.OwnerProfileScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ShiftIQTheme {
        LoginScreen(
            uiState = com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginUiState(
                email = "admin@shiftiq.com"
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onLoginSuccess = {}
        )
    }
}