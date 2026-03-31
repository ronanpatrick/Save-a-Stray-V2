package com.example.save_a_strayv2.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.save_a_strayv2.repository.AuthRepository
import com.example.save_a_strayv2.ui.LoginScreen
import com.example.save_a_strayv2.ui.MainScreen
import com.example.save_a_strayv2.ui.RegistrationScreen

object RootRoutes {
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val MAIN = "main"
}

/**
 * Top-level NavHost that reads Firebase Auth state and switches between
 * Registration, Login, and the Main app.
 */
@Composable
fun RootNavGraph(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val currentUser by authRepository.currentUserFlow.collectAsState(
        initial = authRepository.currentUser
    )

    val startDestination = if (currentUser == null) RootRoutes.LOGIN else RootRoutes.MAIN

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        spring(stiffness = Spring.StiffnessMediumLow)
                    )
        },
        exitTransition = { fadeOut(spring(stiffness = Spring.StiffnessHigh)) },
        popEnterTransition = {
            fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        spring(stiffness = Spring.StiffnessMediumLow)
                    )
        },
        popExitTransition = { fadeOut(spring(stiffness = Spring.StiffnessHigh)) }
    ) {
        // ── Login ────────────────────────────────────────────────────────────
        composable(RootRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(RootRoutes.REGISTER) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Registration ─────────────────────────────────────────────────────
        composable(RootRoutes.REGISTER) {
            RegistrationScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ── Main (bottom-nav scaffold) ───────────────────────────────────────
        composable(RootRoutes.MAIN) {
            MainScreen()
        }
    }

    // ── Reactive auth-state navigation ───────────────────────────────────────
    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentDestination?.route

        if (currentUser != null && currentRoute != RootRoutes.MAIN) {
            // User just signed in / registered → go to Main, clear auth stack
            navController.navigate(RootRoutes.MAIN) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else if (currentUser == null && currentRoute == RootRoutes.MAIN) {
            // User signed out → go back to Login, clear main stack
            navController.navigate(RootRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}
