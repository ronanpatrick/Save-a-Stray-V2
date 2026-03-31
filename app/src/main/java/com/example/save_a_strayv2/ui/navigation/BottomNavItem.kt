package com.example.save_a_strayv2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        label = "Home",
        icon  = Icons.Filled.Home
    )

    data object Match : BottomNavItem(
        route = "match",
        label = "Match",
        icon  = Icons.Filled.Favorite
    )

    data object Messages : BottomNavItem(
        route = "messages",
        label = "Messages",
        icon  = Icons.Filled.Email
    )

    data object Profile : BottomNavItem(
        route = "profile",
        label = "Profile",
        icon  = Icons.Filled.Person
    )

    companion object {
        val all: List<BottomNavItem> = listOf(Home, Match, Messages, Profile)
    }
}
