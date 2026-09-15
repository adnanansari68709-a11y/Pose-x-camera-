package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Immutable, compile-time safe navigation destinations.
 * Structured as an enum to eliminate any class initialization order issues.
 */
enum class AppDestination(
    val route: String,
    val title: String,
    val isBottomNav: Boolean
) {
    HOME("home", "Home", true),
    POSES("poses", "Poses", true),
    CAMERA("camera", "Camera", true),
    FAVORITES("favorites", "Favorites", true),
    PROFILE("profile", "Profile", true),
    PREVIEW("preview", "Preview", false),
    SETTINGS("settings", "Settings", false);

    companion object {
        val bottomNavDestinations: List<AppDestination> by lazy {
            listOf(HOME, POSES, CAMERA, FAVORITES, PROFILE)
        }

        fun fromRoute(route: String?): AppDestination {
            return entries.firstOrNull { it.route == route } ?: HOME
        }
    }
}

/**
 * Visual icons for bottom navigation tabs.
 */
fun AppDestination.selectedIcon(): ImageVector = when (this) {
    AppDestination.HOME -> Icons.Filled.Home
    AppDestination.POSES -> Icons.Filled.ViewList
    AppDestination.CAMERA -> Icons.Filled.CameraAlt
    AppDestination.FAVORITES -> Icons.Filled.Favorite
    AppDestination.PROFILE -> Icons.Filled.Person
    else -> Icons.Filled.Home
}

fun AppDestination.unselectedIcon(): ImageVector = when (this) {
    AppDestination.HOME -> Icons.Outlined.Home
    AppDestination.POSES -> Icons.Outlined.ViewList
    AppDestination.CAMERA -> Icons.Outlined.CameraAlt
    AppDestination.FAVORITES -> Icons.Outlined.FavoriteBorder
    AppDestination.PROFILE -> Icons.Outlined.Person
    else -> Icons.Outlined.Home
}
