package com.example.save_a_strayv2.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.save_a_strayv2.ui.navigation.BottomNavItem
import com.example.save_a_strayv2.ui.screens.HomeScreen
import com.example.save_a_strayv2.ui.screens.MatchScreen
import com.example.save_a_strayv2.ui.screens.MessagesScreen
import com.example.save_a_strayv2.ui.screens.ProfileScreen
import com.example.save_a_strayv2.ui.theme.SaveaStrayV2Theme

/**
 * Root composable that owns the Scaffold, bottom NavigationBar, and NavHost.
 * All top-level destinations are wired here; individual screens live in ui/screens/.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = true,
                enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit    = slideOutVertically(targetOffsetY  = { it }) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(3f)
                ) {
                    BottomNavItem.all.forEach { item ->
                        val isSelected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.route } == true

                        // Subtle spring-scale on selected icon
                        val iconScale by animateFloatAsState(
                            targetValue   = if (isSelected) 1.15f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label         = "iconScale_${item.route}"
                        )

                        NavigationBarItem(
                            selected  = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to the start destination to avoid building up a huge back stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = {
                                Icon(
                                    imageVector        = item.icon,
                                    contentDescription = item.label,
                                    modifier           = Modifier.scale(iconScale)
                                )
                            },
                            label = {
                                Text(
                                    text  = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController    = navController,
                startDestination = BottomNavItem.Home.route
            ) {
                composable(BottomNavItem.Home.route)     { HomeScreen() }
                composable(BottomNavItem.Match.route)    { MatchScreen() }
                composable(BottomNavItem.Messages.route) { MessagesScreen() }
                composable(BottomNavItem.Profile.route)  { 
                    ProfileScreen(
                        onNavigateToAddPet = {
                            navController.navigate("add_pet")
                        }
                    )
                }
                composable("add_pet") {
                    com.example.save_a_strayv2.ui.screens.AddPetScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    SaveaStrayV2Theme {
        MainScreen()
    }
}
